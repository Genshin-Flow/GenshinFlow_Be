package com.next.genshinflow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),
    MEMBER_EXISTS(409, "회원이 존재합니다."),
    NO_PERMISSION(403, "권한이 없습니다."),
    POSTING_NOT_FOUND(404, "포스팅을 찾을 수 없습니다."),
    INVALID_PASSWORD_FORMAT(400, "올바르지 않은 비밀번호 형식입니다."),
    ;

    private final int status;
    private final String message;
}
