package com.next.genshinflow.application.user.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthSignInRequest {
    @Schema(description = "사용자 이메일", type = "String", example = "user@example.com")
    @Email
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;
}
