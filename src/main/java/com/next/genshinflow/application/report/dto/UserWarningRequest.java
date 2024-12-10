package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserWarningRequest {
    @Schema(description = "신고 ID", type = "long")
    @NotNull
    private long reportId;

    @Schema(description = "신고 대상 유저 ID", type = "long")
    private long userId;

    @Schema(description = "경고 사유", type = "String", example = "상업적 / 홍보성")
    private String reason;
}
