package com.next.genshinflow.application.user.dto.mailAuthentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class MailRequest {
    @Schema(description = "사용자 이메일", type = "String", example = "user@example.com")
    @Email
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;
}
