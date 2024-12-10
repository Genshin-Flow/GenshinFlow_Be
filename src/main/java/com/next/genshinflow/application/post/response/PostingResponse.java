package com.next.genshinflow.application.post.response;

import com.next.genshinflow.enumeration.QuestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record PostingResponse(
    @Schema(description = "포스트 id", type = "long", example = "1")
    long id,
    String assigner,
    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "something")
    QuestCategory category,
    @Schema(description = "월드 레벨", type = "int", example = "7")
    int wordLevel,
    @Schema(description = "이야기 (요청글 제목)", type = "String", example = "집가고싶어요")
    String title,
    @Schema(description = "등록일시", type = "LocalDateTime", example = "2024-08-12 01:12:00")
    LocalDateTime registeredAt
) {

}
