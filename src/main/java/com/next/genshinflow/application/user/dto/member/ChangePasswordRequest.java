package com.next.genshinflow.application.user.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordRequest {
    @Schema(description = "사용자 이메일", type = "String", example = "user@example.com")
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email
    private String email;

    @Schema(description = "이메일 인증 번호", type = "String", example = "199494")
    @NotBlank(message = "인증 번호를 입력해 주세요")
    private String authNum;

    @Schema(description = "사용자 비밀번호", type = "String", example = "example1234!")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}
