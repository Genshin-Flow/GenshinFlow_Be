package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.dto.auth.*;
import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.jwt.TokenProvider;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final MailSendService mailSendService;
    private final EnkaService enkaService;
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 일반 유저 회원가입
    public MemberResponse createMember(SignUpRequest signUpRequest) {
        mailSendService.verifyAuthCode(signUpRequest.getEmail(), signUpRequest.getAuthNum());
        return saveNewMember(signUpRequest.getUid(), signUpRequest.getEmail(), signUpRequest.getPassword(), false);
    }

    // OAuth 회원가입
    public MemberResponse createOAuthMember(OAuthSignUpRequest signUpRequest) {
        return saveNewMember(signUpRequest.getUid(), signUpRequest.getEmail(), null, true);
    }

    private MemberResponse saveNewMember(long uid, String email, String password, boolean oauth) {
        verifyExistEmail(email);

        UserInfoResponse apiResponse = enkaService.getUserInfoFromApi(uid);
        MemberEntity member = buildMemberEntity(uid, email, password, apiResponse, oauth);
        MemberEntity savedMember = memberRepository.save(member);

        log.info("User {} successfully signed up.", email);
        return MemberMapper.memberToResponse(savedMember);
    }

    // 일반 유저 로그인
    public TokenResponse authenticate(LoginRequest loginRequest) {
        // 로그인 실패 횟수 증가 / 횟수 초과 검증
        MemberEntity member = handleFailedLoginAttempts(loginRequest);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 공통 후처리 작업
        processPostLoginTasks(member, authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        return new TokenResponse(accessToken, redisRepository.getData(member.getEmail()));
    }

    // OAuth 로그인
    public TokenResponse authenticateWithOAuth(OAuthSignInRequest oAuthLoginRequest, String provider) {
        MemberEntity member = findMember(oAuthLoginRequest.getEmail());

        if (!member.getOAuthUser()) {
            throw new BusinessLogicException(ExceptionCode.USER_CANNOT_LOGIN_WITH_OAUTH);
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Map<String, Object> attributes = Map.of("email", member.getEmail());
        OAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "email");

        OAuth2AuthenticationToken authentication =
            new OAuth2AuthenticationToken(oAuth2User, authorities, provider);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 공통 후처리 작업
        processPostLoginTasks(member, authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        return new TokenResponse(accessToken, redisRepository.getData(member.getEmail()));
    }

    private void processPostLoginTasks(MemberEntity member, Authentication authentication) {
        // 게임 상의 프로필 정보 변경 시 업데이트
        enkaService.updateMemberIfPlayerInfoChanged(member);

        // 제재 유저일 시 제재 기간 확인 및 복구
        checkAndUpdateDisciplinaryStatus(member.getId());

        // 로그인 성공 시 Redis에 RefreshToken 저장
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        redisRepository.setData(member.getEmail(), refreshToken, Duration.ofDays(7));

        // 로그인 실패 횟수 초기화
        member.setFailedLoginAttempts(0);
        memberRepository.save(member);
    }

    private MemberEntity handleFailedLoginAttempts(LoginRequest loginRequest) {
        MemberEntity member = memberRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS));

        if (member.getFailedLoginAttempts() >= 5) {
            throw new BusinessLogicException(ExceptionCode.LOGIN_ATTEMPTS_EXCEEDED);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            member.setFailedLoginAttempts(member.getFailedLoginAttempts() + 1);
            memberRepository.save(member);
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        return member;
    }

    // 리프레시 토큰을 통한 액세스 토큰 재발급
    public TokenResponse refreshAccessToken(String accessToken, String refreshToken) throws IOException {
        if (!tokenProvider.validateToken(accessToken)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_ACCESS_TOKEN);
        }

        String email = tokenProvider.getUserInfoFromToken(accessToken).getSubject();
        String refreshTokenFromRedis = redisRepository.getData(email);

        if (refreshTokenFromRedis == null || !refreshTokenFromRedis.equals(refreshToken)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String newAccessToken = tokenProvider.generateAccessToken(authentication);

        return new TokenResponse(newAccessToken, refreshToken);
    }

    // 일반 유저 비밀번호 변경
    public void changePassword(String email, String authNum, String password) {
        MemberEntity member = findMember(email);
        mailSendService.verifyAuthCode(email, authNum);

        if (passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessLogicException(ExceptionCode.SAME_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(password));
        member.setFailedLoginAttempts(0);
        memberRepository.save(member);
    }

    private MemberEntity buildMemberEntity(long uid, String email, String password, UserInfoResponse apiResponse, boolean oauth) {
        String profileImgPath = enkaService.getIconPathForProfilePicture(apiResponse.getPlayerInfo().getProfilePicture().getId());
        String tower = apiResponse.getPlayerInfo().getTowerFloorIndex() + "-" + apiResponse.getPlayerInfo().getTowerLevelIndex();

        MemberEntity member = MemberMapper.toMember(uid, email, apiResponse, profileImgPath, tower, oauth);

        if (password != null) {
            member.setPassword(passwordEncoder.encode(password));
        }

        return member;
    }

    // 로그인 시 Role 확인 및 복구 로직
    public void checkAndUpdateDisciplinaryStatus(long userId) {
        MemberEntity member = findMember(userId);
        Role currentRole = member.getRole();

        if (!currentRole.isSuspended()) return;

        LocalDateTime disciplineDate = member.getDisciplineDate();
        LocalDateTime endOfSuspension = disciplineDate.plusDays(currentRole.getSuspensionDays());
        LocalDateTime currentDate = LocalDateTime.now();

        if (currentDate.isAfter(endOfSuspension)) {
            member.setRole(Role.USER);
            member.setDisciplineDate(null);
            memberRepository.save(member);
        }
    }

    // 이메일 검증
    public void verifyExistEmail(String email) {
        Optional<MemberEntity> member = memberRepository.findByEmail(email);
        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    public MemberEntity findMember(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public MemberEntity findMember(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    // 로그인한 사용자 추출
    public MemberEntity getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return findMember(user.getUsername());
        }

        return null;
    }
}
