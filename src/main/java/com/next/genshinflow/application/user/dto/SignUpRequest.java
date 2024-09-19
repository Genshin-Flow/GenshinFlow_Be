package com.next.genshinflow.application.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotNull
    private long uid;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*[0-9])|(?=.*[A-Za-z])(?=.*[!@#$%^&*(),.?\":{}|<>])|(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
        message = "비밀번호는 문자, 숫자, 기호 중 두 가지 이상을 포함하고 8자 이상이어야 합니다."
    )
    private String password;
}
