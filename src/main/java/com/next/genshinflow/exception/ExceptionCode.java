package com.next.genshinflow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),
    MEMBER_EXISTS(409, "회원이 존재합니다."),
    NO_PERMISSION(403, "권한이 없습니다."),
    UNAUTHORIZED_USER(401, "인증되지 않은 사용자입니다."),
    JWT_TOKEN_EXPIRED(404, "만료된 JWT 토큰입니다."),
    JWT_SIGNATURE_INVALID(400, "잘못된 JWT 서명입니다."),
    JWT_TOKEN_UNSUPPORTED(400, "지원되지 않는 JWT 토큰입니다."),
    JWT_TOKEN_MALFORMED(400, "잘못된 JWT 입력값이 제공되었습니다."),
    INVALID_REFRESH_TOKEN(400, "유효하지 않은 리프레시 토큰입니다."),

    POSTING_NOT_FOUND(404, "포스팅을 찾을 수 없습니다."),

    INVALID_PASSWORD(403, "올바르지 않은 비밀번호입니다."),
    INVALID_PASSWORD_FORMAT(400, "올바르지 않은 비밀번호 형식입니다."),
    ;
    private final int status;
    private final String message;
}
