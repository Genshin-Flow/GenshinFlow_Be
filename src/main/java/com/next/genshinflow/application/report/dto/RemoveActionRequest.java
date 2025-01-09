package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class RemoveActionRequest {
    @Schema(description = "유저 Email", type = "String", example = "moshi@gmail.com")
    @NotBlank(message = "대상 Email을 입력해 주세요.")
    private String userEmail;

    @Schema(description = "신고 Id", type = "long")
    @Positive(message = "신고 Id를 입력해 주세요.")
    private Long reportId;
}
