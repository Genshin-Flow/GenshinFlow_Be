package com.next.genshinflow.application.user.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthSignUpRequest {
    @Schema(description = "원신 UID", type = "long", example = "1800812993")
    @Positive(message = "UID를 입력해 주세요.")
    private long uid;

    @Schema(description = "사용자 이메일", type = "String", example = "user@example.com")
    @Email
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;
}
