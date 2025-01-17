package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.EntityFinder;
import com.next.genshinflow.application.user.dto.auth.TokenResponse;
import com.next.genshinflow.application.validation.UserValidationManager;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final UserValidationManager validationManager;
    private final EntityFinder entityFinder;


    // 리프레시 토큰을 통한 액세스 토큰 재발급
    public TokenResponse refreshAccessToken(String refreshToken) {
        validationManager.validateMatchingRefreshToken(refreshToken);

        String subject = tokenProvider.getSubjectFromRefreshToken(refreshToken);
        MemberEntity member = entityFinder.findMemberByEmail(subject);

        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(member.getRole().getRole())
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            subject, null, authorities
        );

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        return new TokenResponse(newAccessToken, refreshToken);
    }
}
