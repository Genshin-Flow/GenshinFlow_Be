package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DisciplinaryActionRequest {
    @Schema(description = "신고 ID", type = "long")
    @NotNull
    private long reportId;

    @Schema(description = "유저 ID", type = "long")
    @NotNull
    private long userId;

    @Schema(description = "제재 항목", type = "String", example = "1일 정지")
    @NotBlank
    private String disciplinaryAction;

    @Schema(description = "제재 사유", type = "String", example = "상업적 / 홍보성")
    private String reason;
}
