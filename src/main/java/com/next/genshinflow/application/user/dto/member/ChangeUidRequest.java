package com.next.genshinflow.application.user.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUidRequest {
    @Schema(description = "유저 Email", type = "String", example = "moshi@gmail.com")
    private String userEmail;

    @Schema(description = "원신 UID", type = "long", example = "1800812993")
    private long uid;
}
