package com.next.genshinflow.application.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RemoveActionRequest {
    @Schema(description = "유저 Email", type = "String", example = "moshi@gmail.com")
    private String userEmail;

    @Schema(description = "신고 ID", type = "long")
    private Long reportId;
}
