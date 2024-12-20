package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateReportRequest {
    @Schema(description = "신고의 대상이 된 사용자 Email", type = "String", example = "moshi@gmail.com")
    @NotBlank
    private String targetUserEmail;

    @Schema(description = "신고 내용", type = "String", example = "상업적/홍보성")
    @NotBlank
    private String content;

    @Schema(description = "신고 이미지들", type = "List<String>")
    private List<String> images;
}
