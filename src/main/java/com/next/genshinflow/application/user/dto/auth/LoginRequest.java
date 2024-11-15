package com.next.genshinflow.application.user.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @Schema(description = "사용자 이메일", type = "String", example = "user@example.com")
    @NotBlank
    private String email;

    @Schema(description = "사용자 비밀번호", type = "String", example = "example1234!")
    @NotBlank
    private String password;
}
