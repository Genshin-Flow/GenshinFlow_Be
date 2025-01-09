package com.next.genshinflow.application.validation;

import com.next.genshinflow.application.user.dto.auth.LoginRequest;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidationManager {
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 유저 회원가입 검증
    public void validateCreateUser(String email, long uid) {
        ensureEmailDoesNotExist(email);
        ensureUidDoesNotExist(uid);
    }

    // 로그인 시도 횟수 검증
    public MemberEntity handleFailedLoginAttempts(LoginRequest request) {
        MemberEntity member = ensureEmailExists(request.getEmail());

        if (member.getFailedLoginAttempts() >= 5)
            throw new BusinessLogicException(ExceptionCode.LOGIN_ATTEMPTS_EXCEEDED);

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            member.setFailedLoginAttempts(member.getFailedLoginAttempts() + 1);
            memberRepository.save(member);
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        return member;
    }

    // 일반 유저는 OAuth 로그인 접근 금지
    public void validateNonOAuthUserLogin(boolean isOAuthUser) {
        if (!isOAuthUser) {
            log.error("OAuth login validation failed: Non-OAuth user attempted to log in with OAuth.");
            throw new BusinessLogicException(ExceptionCode.USER_CANNOT_LOGIN_WITH_OAUTH);
        }
    }

    // 리프레시 토큰 검증
    public void validateMatchingRefreshToken(String refreshToken) {
        String refreshTokenFromRedis = redisRepository.getData(refreshToken);
        System.out.println(refreshTokenFromRedis);

        if (refreshTokenFromRedis == null) {
            log.error("Refresh token not found in Redis: {}", refreshToken);
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }
    }

    // 변경하려는 비밀번호와 현재 비밀번호가 같은지 검사
    public void validatePasswordNotSame(String newPassword, String currentPassword) {
        if (passwordEncoder.matches(newPassword, currentPassword)) {
            log.error("Password validation failed: New password is the same as the current password.");
            throw new BusinessLogicException(ExceptionCode.SAME_PASSWORD);
        }
    }

    // 이메일 인증 코드 검증
    public void verifyAuthCode(String email, String authNum) {
        String storedEmail = redisRepository.getData(authNum);

        if (storedEmail == null) {
            log.warn("Verification failed: authNum {} does not exist in Redis", authNum);
            throw new BusinessLogicException(ExceptionCode.INVALID_AUTH_CODE);
        }

        if (!storedEmail.equals(email)) {
            log.warn("Verification failed: authNum {} mismatch for provided email. Stored email is different.", authNum);
            throw new BusinessLogicException(ExceptionCode.INVALID_AUTH_CODE);
        }
    }

    // 이미 존재하는 회원인지 검사
    private void ensureEmailDoesNotExist(String email) {
        Optional<MemberEntity> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            log.error("Email validation failed: Email {} already exists.", email);
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    // 이메일(아이디) 가 잘못되었는지 검사
    private MemberEntity ensureEmailExists(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> {
                log.error("Email validation failed: Email {} does not exist.", email);
                return new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
            });
    }

    // 이미 존재하는 uid인지 검사
    private void ensureUidDoesNotExist(long uid) {
        if (memberRepository.existsByUid(uid)) {
            log.error("UID validation failed: UID {} already exists.", uid);
            throw new BusinessLogicException(ExceptionCode.UID_ALREADY_EXISTS);
        }
    }
}
