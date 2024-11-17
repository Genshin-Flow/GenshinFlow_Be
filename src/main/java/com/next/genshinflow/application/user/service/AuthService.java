package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.dto.auth.*;
import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.enumeration.AccountStatus;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    public MemberResponse createMember(SignUpRequest signUpRequest) {
        mailSendService.verifyAuthCode(signUpRequest.getEmail(), signUpRequest.getAuthNum());
        verifyExistEmail(signUpRequest.getEmail());

        UserInfoResponse apiResponse = getUserInfoFromApi(signUpRequest.getUid());

        MemberEntity member = buildMemberEntity(signUpRequest, apiResponse, Role.USER);
        MemberEntity savedMember = memberRepository.save(member);

        return MemberMapper.memberToResponse(savedMember);
    }

    public TokenResponse authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MemberEntity findMember = findMember(loginRequest.getEmail());
        UserInfoResponse apiResponse = getUserInfoFromApi(findMember.getUid());

        updateMemberIfChanged(findMember, apiResponse);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        redisRepository.setData(loginRequest.getEmail(), refreshToken, Duration.ofDays(7));

        return new TokenResponse(accessToken, refreshToken);
    }

    // OAuth 회원가입
    public MemberResponse createOAuthMember(OAuthSignUpRequest signUpRequest) {
        verifyExistEmail(signUpRequest.getEmail());

        UserInfoResponse apiResponse = getUserInfoFromApi(signUpRequest.getUid());

        MemberEntity member = buildOAuthMemberEntity(signUpRequest, apiResponse, Role.OAUTH_USER);
        MemberEntity savedMember = memberRepository.save(member);

        return MemberMapper.memberToResponse(savedMember);
    }

    // OAuth 로그인
    public TokenResponse authenticateWithOAuth(OAuthSignInRequest oAuthLoginRequest, String provider) {
        MemberEntity findMember = findMember(oAuthLoginRequest.getEmail());

        if (findMember.getRole() != Role.OAUTH_USER) {
            throw new BusinessLogicException(ExceptionCode.USER_CANNOT_LOGIN_WITH_OAUTH);
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Map<String, Object> attributes = Map.of("email", findMember.getEmail());
        OAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "email");

        OAuth2AuthenticationToken authenticationToken =
            new OAuth2AuthenticationToken(oAuth2User, authorities, provider);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        UserInfoResponse apiResponse = getUserInfoFromApi(findMember.getUid());
        updateMemberIfChanged(findMember, apiResponse);

        String accessToken = tokenProvider.generateAccessToken(authenticationToken);
        String refreshToken = tokenProvider.generateRefreshToken(authenticationToken);

        redisRepository.setData(oAuthLoginRequest.getEmail(), refreshToken, Duration.ofDays(7));

        return new TokenResponse(accessToken, refreshToken);
    }

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

    public void changePassword(String email, String authNum, String password) {
        MemberEntity member = findMember(email);
        mailSendService.verifyAuthCode(email, authNum);

        if (passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessLogicException(ExceptionCode.SAME_PASSWORD);
        }

        member.setPassword(passwordEncoder.encode(password));
        memberRepository.save(member);

        log.info("Password changed for member: {}", member.getEmail());
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

    public UserInfoResponse getUserInfoFromApi(long uid) {
        UserInfoResponse apiResponse = enkaService.fetchUserInfoFromApi(uid);

        if (apiResponse == null || apiResponse.getPlayerInfo() == null) {
            throw new BusinessLogicException(ExceptionCode.EXTERNAL_API_ERROR);
        }
        return apiResponse;
    }

    public void updateMemberIfChanged(MemberEntity member, UserInfoResponse apiResponse) {
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

    private MemberEntity buildMemberEntity(
        SignUpRequest signUpRequest,
        UserInfoResponse apiResponse,
        Role role) {

        return buildMemberEntity(signUpRequest.getUid(), signUpRequest.getEmail(), signUpRequest.getPassword(), apiResponse, role);
    }

    private MemberEntity buildOAuthMemberEntity(
        OAuthSignUpRequest signUpRequest,
        UserInfoResponse apiResponse,
        Role role) {

        return buildMemberEntity(signUpRequest.getUid(), signUpRequest.getEmail(), null, apiResponse, role);
    }

    private MemberEntity buildMemberEntity(long uid, String email, String password, UserInfoResponse apiResponse, Role role) {
        String profileImgPath = enkaService.getIconPathForProfilePicture(apiResponse.getPlayerInfo().getProfilePicture().getId());
        String tower = apiResponse.getPlayerInfo().getTowerFloorIndex() + "-" + apiResponse.getPlayerInfo().getTowerLevelIndex();

        MemberEntity.MemberEntityBuilder builder = MemberEntity.builder()
            .uid(uid)
            .name(apiResponse.getPlayerInfo().getNickname())
            .email(email)
            .image(profileImgPath)
            .level(apiResponse.getPlayerInfo().getLevel())
            .worldLevel(apiResponse.getPlayerInfo().getWorldLevel())
            .towerLevel(tower)
            .status(AccountStatus.ACTIVE_USER)
            .role(role);

        if (password != null) {
            builder.password(passwordEncoder.encode(password));
        }

        return builder.build();
    }
}
