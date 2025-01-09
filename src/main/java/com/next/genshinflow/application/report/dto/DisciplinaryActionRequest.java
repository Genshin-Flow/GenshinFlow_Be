package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class DisciplinaryActionRequest {
    @Schema(description = "신고 Id", type = "long")
    @Positive(message = "신고 Id를 입력해 주세요.")
    private long reportId;

    @Schema(description = "유저 Email", type = "String", example = "moshi@gmail.com")
    @NotBlank(message = "유저(제재 대상) Email을 입력해 주세요.")
    private String userEmail;

    @Schema(description = "제재 항목", type = "String", example = "1일 정지")
    @NotBlank(message = "제재 항목을 입력해 주세요.")
    private String disciplinaryAction;

    @Schema(description = "제재 사유", type = "String", example = "상업적 / 홍보성")
    @NotBlank(message = "제재 사유를 입력해 주세요.")
    private String reason;
}
