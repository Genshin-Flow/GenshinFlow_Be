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
        verifyExistEmail(signUpRequest.getEmail());

        UserInfoResponse apiResponse = getUserInfoFromApi(signUpRequest.getUid());

        MemberEntity member = buildMemberEntity(
            signUpRequest.getUid(),
            signUpRequest.getEmail(),
            signUpRequest.getPassword(),
            apiResponse,
            false);

        MemberEntity savedMember = memberRepository.save(member);

        return MemberMapper.memberToResponse(savedMember);
    }

    // 일반 유저 로그인
    public TokenResponse authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MemberEntity findMember = findMember(loginRequest.getEmail());

        // 게임 상의 프로필 정보 변경 시 업데이트
        UserInfoResponse apiResponse = getUserInfoFromApi(findMember.getUid());
        updateMemberIfPlayerInfoChanged(findMember, apiResponse);

        // 제재 유저일 시 제재 기간 확인 및 복구
        checkAndUpdateDisciplinaryStatus(findMember.getId());

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        redisRepository.setData(loginRequest.getEmail(), refreshToken, Duration.ofDays(7));

        return new TokenResponse(accessToken, refreshToken);
    }

    // OAuth 회원가입
    public MemberResponse createOAuthMember(OAuthSignUpRequest signUpRequest) {
        verifyExistEmail(signUpRequest.getEmail());

        UserInfoResponse apiResponse = getUserInfoFromApi(signUpRequest.getUid());

        MemberEntity member = buildMemberEntity(
            signUpRequest.getUid(),
            signUpRequest.getEmail(),
            null,
            apiResponse,
            true);

        MemberEntity savedMember = memberRepository.save(member);

        return MemberMapper.memberToResponse(savedMember);
    }

    // OAuth 로그인
    public TokenResponse authenticateWithOAuth(OAuthSignInRequest oAuthLoginRequest, String provider) {
        MemberEntity findMember = findMember(oAuthLoginRequest.getEmail());

        if (!findMember.getOAuthUser()) {
            throw new BusinessLogicException(ExceptionCode.USER_CANNOT_LOGIN_WITH_OAUTH);
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Map<String, Object> attributes = Map.of("email", findMember.getEmail());
        OAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "email");

        OAuth2AuthenticationToken authenticationToken =
            new OAuth2AuthenticationToken(oAuth2User, authorities, provider);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 게임 상의 프로필 정보 변경 시 업데이트
        UserInfoResponse apiResponse = getUserInfoFromApi(findMember.getUid());
        updateMemberIfPlayerInfoChanged(findMember, apiResponse);

        // 제재 유저일 시 제재 기간 확인 및 복구
        checkAndUpdateDisciplinaryStatus(findMember.getId());

        String accessToken = tokenProvider.generateAccessToken(authenticationToken);
        String refreshToken = tokenProvider.generateRefreshToken(authenticationToken);

        redisRepository.setData(oAuthLoginRequest.getEmail(), refreshToken, Duration.ofDays(7));

        return new TokenResponse(accessToken, refreshToken);
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
        memberRepository.save(member);
    }

    // 원신 api 유저 정보 요청
    public UserInfoResponse getUserInfoFromApi(long uid) {
        UserInfoResponse apiResponse = enkaService.fetchUserInfoFromApi(uid);

        if (apiResponse == null || apiResponse.getPlayerInfo() == null) {
            throw new BusinessLogicException(ExceptionCode.EXTERNAL_API_ERROR);
        }
        return apiResponse;
    }

    // 받아온 api를 조회해 변경된 유저 정보가 있으면 업데이트
    public void updateMemberIfPlayerInfoChanged(MemberEntity member, UserInfoResponse apiResponse) {
        UserInfoResponse.PlayerInfo playerInfo = apiResponse.getPlayerInfo();
        if (playerInfo == null) return;
        boolean isUpdated = false;

        if (!member.getName().equals(playerInfo.getNickname())) {
            member.setName(playerInfo.getNickname());
            isUpdated = true;
        }
        if (member.getLevel() != playerInfo.getLevel()) {
            member.setLevel(playerInfo.getLevel());
            isUpdated = true;
        }
        if (member.getWorldLevel() != playerInfo.getWorldLevel()) {
            member.setWorldLevel(playerInfo.getWorldLevel());
            isUpdated = true;
        }

        String tower = playerInfo.getTowerFloorIndex() + "-" + playerInfo.getTowerLevelIndex();
        if (!member.getTowerLevel().equals(tower)) {
            member.setTowerLevel(tower);
            isUpdated = true;
        }

        if (isUpdated) {
            memberRepository.save(member);
        }
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
