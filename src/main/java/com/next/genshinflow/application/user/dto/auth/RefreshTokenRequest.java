package com.next.genshinflow.application.user.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    @Schema(description = "Refresh Token", type = "String")
    @NotBlank(message = "Refresh Token을 입력해 주세요.")
    private String refreshToken;
}
