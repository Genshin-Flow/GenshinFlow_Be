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
    INVALID_ACCESS_TOKEN(400, "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(400, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_AUTH_CODE(400, "잘못된 인증 코드입니다."),
    EXTERNAL_API_ERROR(500, "외부 API 호출에 실패했습니다."),
    SAME_PASSWORD(400, "새 비밀번호는 현재 비밀번호와 같을 수 없습니다."),
    USER_CANNOT_LOGIN_WITH_OAUTH(403, "일반 유저는 OAuth로 로그인할 수 없습니다."),
    REPORT_NOT_FOUND(404, "신고 항목을 찾을 수 없습니다."),
    CANNOT_REPORT_YOURSELF(400, "자기 자신을 신고할 수 없습니다."),
    WARNING_NOT_FOUND(404, "해당 reportId는 경고 내역에 없습니다."),
    DISCIPLINE_NOT_FOUND(404, "해당 reportId는 제재 내역에 없습니다."),
    INVALID_DISCIPLINARY_ACTION(400, "유효하지 않은 징계 사항입니다."),
    POST_NOT_FOUND(404, "게시물을 찾을 수 없습니다."),
    INVALID_POST_PASSWORD(400, "잘못된 게시글 비밀번호입니다.");

    private final int status;
    private final String message;

    public int getCode() {
        return status;
    }
}
