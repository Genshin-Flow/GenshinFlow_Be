package com.next.genshinflow.application.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @Schema(description = "원신 UID", type = "long", example = "1800812993")
    @NotNull
    private long uid;

    @Schema(description = "사용자 이메일", type = "String", example = "user@example.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "사용자 비밀번호", type = "String", example = "example1234!")
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*[0-9])|(?=.*[A-Za-z])(?=.*[!@#$%^&*(),.?\":{}|<>])|(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
        message = "비밀번호는 문자, 숫자, 기호 중 두 가지 이상을 포함하고 8자 이상이어야 합니다."
    )
    private String password;
}
