package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class UserWarningRequest {
    @Schema(description = "신고 ID", type = "long")
    @Positive(message = "신고 Id를 입력해 주세요.")
    private long reportId;

    @Schema(description = "신고 대상 유저 Email", type = "String", example = "moshi@gmail.com")
    @NotBlank(message = "대상 Email을 입력해 주세요.")
    private String userEmail;

    @Schema(description = "경고 사유", type = "String", example = "상업적 / 홍보성")
    @NotBlank(message = "경고 사유를 입력해 주세요.")
    private String reason;
}
