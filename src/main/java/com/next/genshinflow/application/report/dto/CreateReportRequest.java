package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateReportRequest {
    @Schema(description = "신고를 제기한 사용자", type = "long", example = "123")
    @NotNull
    private Long reportingUserId;

    @Schema(description = "신고의 대상이 된 사용자", type = "long", example = "123")
    @NotNull
    private Long targetUserId;

    @Schema(description = "신고 내용", type = "String", example = "상업적/홍보성")
    @NotBlank
    private String content;

    @Schema(description = "신고 이미지", type = "String")
    private String image;
}
