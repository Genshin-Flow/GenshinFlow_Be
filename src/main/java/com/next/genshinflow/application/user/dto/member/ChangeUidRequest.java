package com.next.genshinflow.application.user.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUidRequest {
    @Schema(description = "유저 Email", type = "String", example = "moshi@gmail.com")
    @Email
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String userEmail;

    @Schema(description = "원신 UID", type = "long", example = "1800812993")
    @Positive(message = "UID를 입력해 주세요.")
    private long uid;
}
