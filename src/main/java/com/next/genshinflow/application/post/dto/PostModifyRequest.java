package com.next.genshinflow.application.post.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class PostModifyRequest {
    @Schema(description = "포스트 Id", type = "long", example = "1")
    @Positive(message = "포스트 Id를 입력해 주세요.")
    private final long postId;

    @Schema(description = "비회원 게시물 비밀번호 (비회원일 경우만)", type = "String", example = "abcd1234!")
    private final String password;

    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "something")
    private final QuestCategory questCategory;

    @Schema(description = "게시물 내용", type = "String", example = "날 구원해 줄 사람 어디 없나")
    private final String content;

    @Schema(description = "자동 완료 시간", type = "int", example = "60")
    @Min(value = 30, message = "자동 완료 시간은 30분 이상이어야 합니다.")
    private final int autoCompleteTime;

    @JsonCreator
    public PostModifyRequest(
        @JsonProperty("postId") long postId,
        @JsonProperty("password") String password,
        @JsonProperty("questCategory") String questCategory,
        @JsonProperty("content") String content,
        @JsonProperty("autoCompleteTime") int autoCompleteTime
    ) {
        this.postId = postId;
        this.password = password;
        this.questCategory = QuestCategory.fromCategoryName(questCategory);
        this.content = content;
        this.autoCompleteTime = autoCompleteTime;
    }
}
