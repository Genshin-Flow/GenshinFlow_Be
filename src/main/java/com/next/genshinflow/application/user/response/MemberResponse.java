package com.next.genshinflow.application.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.next.genshinflow.enumeration.AccountStatus;
import com.next.genshinflow.enumeration.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberResponse(
    @JsonIgnore
    @Schema(description = "유저 id", type = "long", example = "1")
    long id,
    @Schema(description = "유저 uid", type = "long", example = "1")
    long uid,
    @Schema(description = "닉네임", type = "String", example = "nickname")
    String name,
    @Schema(description = "이메일", type = "String", example = "example@gmail.com")
    String email,
    @Schema(description = "비밀번호", type = "String", example = "example1234!")
    String password,
    @Schema(description = "프로필 사진", type = "String", example = "example.jpg")
    String profileImg,
    @Schema(description = "유저 상태", type = "enum", example = "1주일 정지, 휴면 계정")
    AccountStatus status,
    @Schema(description = "유저 권한", type = "enum", example = "유저 혹은 관리자")
    Role role
) {

}
