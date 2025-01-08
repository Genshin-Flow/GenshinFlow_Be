package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.dto.auth.TokenResponse;
import com.next.genshinflow.application.validation.UserValidationManager;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisRepository redisRepository;
    private final TokenProvider tokenProvider;
    private final UserValidationManager validationManager;


    // 리프레시 토큰을 통한 액세스 토큰 재발급
    public TokenResponse refreshAccessToken(String accessToken, String refreshToken) {
        validationManager.validateValidAccessToken(accessToken);

        String email = tokenProvider.getUserInfoFromToken(accessToken).getSubject();
        String refreshTokenFromRedis = redisRepository.getData(email);

        validationManager.validateMatchingRefreshToken(refreshToken, refreshTokenFromRedis);

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String newAccessToken = tokenProvider.generateAccessToken(authentication);

        return new TokenResponse(newAccessToken, refreshToken);
    }
}
