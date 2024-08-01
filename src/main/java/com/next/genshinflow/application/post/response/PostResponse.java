package com.next.genshinflow.application.post.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record PostResponse(
    @Schema(description = "포스트 id", type = "long", example = "1")
    long id,
    // TODO: member/user 작업시 수정
    // @Schema(description = "포스트 id", type = "long", example = "1")
    String assigner,
    // TODO: member/entity 작업 분 merge 시 수정
    // @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "something")
    // QuestCategory category
    String category,
    @Schema(description = "월드 레벨", type = "int", example = "7")
    int wordLevel,
    @Schema(description = "이야기 (요청글 제목)", type = "String", example = "집가고싶어요")
    String title,
    @Schema(description = "등록일시", type = "LocalDateTime", example = "2024-08-12 01:12:00")
    LocalDateTime registeredAt
) {

}
