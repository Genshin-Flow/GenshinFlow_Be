package com.next.genshinflow.application.report.dto;

import com.next.genshinflow.enumeration.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    @Schema(description = "report ID", type = "long", example = "123")
    private Long id;

    @Schema(description = "신고를 제기한 사용자 Email", type = "String", example = "moshi@gmail.com")
    private String reportingUserEmail;

    @Schema(description = "신고의 대상이 된 사용자 Email", type = "String", example = "moshi@gmail.com")
    private String targetUserEmail;

    @Schema(description = "신고 내용", type = "String", example = "상업적/홍보성")
    private String content;

    @Schema(description = "신고 이미지들", type = "List<String>")
    private List<String> images;

    @Schema(description = "신고 처리 상태", type = "enum", example = "처리됨")
    private ReportStatus reportStatus;

    @Schema(description = "생성 날짜", type = "String")
    private LocalDateTime createdAt;

    @Schema(description = "신고 처리 날짜", type = "String")
    private LocalDateTime completedAt;
}
