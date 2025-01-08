package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.EntityFinder;
import com.next.genshinflow.application.user.dto.auth.LoginRequest;
import com.next.genshinflow.application.user.dto.auth.OAuthSignInRequest;
import com.next.genshinflow.application.user.dto.auth.TokenResponse;
import com.next.genshinflow.application.validation.UserValidationManager;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignInService {
    private final EntityFinder entityFinder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserValidationManager validationManager;
    private final TokenProvider tokenProvider;
    private final RedisRepository redisRepository;
    private final MemberRepository memberRepository;
    private final UserProfileService userProfileService;

    // 일반 유저 로그인
    public TokenResponse authenticate(LoginRequest loginRequest) {
        // 로그인 실패 횟수 증가 / 횟수 초과 검증
        MemberEntity member = validationManager.handleFailedLoginAttempts(loginRequest);

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
        MemberEntity member = entityFinder.findMemberByEmail(oAuthLoginRequest.getEmail());
        validationManager.validateNonOAuthUserLogin(member.getOAuthUser());

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
        userProfileService.updateMemberIfPlayerInfoChanged(member);
        // 제재 유저일 시 제재 기간 확인 및 복구
        checkAndUpdateDisciplinaryStatus(member.getId());
        // 로그인 성공 시 Redis에 RefreshToken 저장
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        redisRepository.setData(member.getEmail(), refreshToken, Duration.ofDays(7));
        // 로그인 실패 횟수 초기화
        member.setFailedLoginAttempts(0);
        memberRepository.save(member);
    }

    // 로그인 시 Role 확인 및 복구 로직
    private void checkAndUpdateDisciplinaryStatus(long userId) {
        MemberEntity member = entityFinder.findMemberById(userId);
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
}
