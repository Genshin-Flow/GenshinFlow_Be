package com.next.genshinflow.application.post.dto;

import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostModifyRequest {
    @Schema(description = "포스트 id", type = "long", example = "1")
    @NotNull
    private long postId;

    @Schema(description = "비회원 게시물 비밀번호 (비회원일 경우만)", type = "String", example = "abcd1234!")
    private String password;

    @Schema(description = "이야기 (요청글 제목)", type = "String", example = "집가고싶어요")
    private String title;

    @Schema(description = "작성자 uid (비회원일 경우만)", type = "long", example = "123415456446")
    private long uid;

    @Schema(description = "서버", type = "Region", example = "ASIA")
    private Region region;

    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "something")
    private QuestCategory questCategory;

    @Schema(description = "월드 레벨", type = "int", example = "7")
    private int wordLevel;

    @Schema(description = "게시물 내용", type = "String", example = "날 구원해 줄 사람 어디 없나")
    private String content;

    @Schema(description = "자동 완료 시간", type = "int", example = "60")
    @Min(30)
    private int autoCompleteTime;
}
