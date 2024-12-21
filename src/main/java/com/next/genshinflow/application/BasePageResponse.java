package com.next.genshinflow.application;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record BasePageResponse<T>(
    List<T> content,
    @Schema(description = "현재 페이지", example = "1") int page,
    @Schema(description = "페이지 크기", example = "10") int size,
    @Schema(description = "전체 항목 수", example = "80") long totalElements,
    @Schema(description = "전체 페이지 수", example = "8") int totalPages
) {}
