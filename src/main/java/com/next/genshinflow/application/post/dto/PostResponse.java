package com.next.genshinflow.application.post.dto;

import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class PostResponse {
    @Schema(description = "포스트 id", type = "long", example = "1")
    private long id;
    @Schema(description = "작성자 Email", type = "String", example = "namdogom@gmail.com")
    private String writerEmail;
    @Schema(description = "이야기 (요청글 제목)", type = "String", example = "집가고싶어요")
    private String title;
    @Schema(description = "작성자 uid", type = "long", example = "123415456446")
    private long uid;
    @Schema(description = "서버", type = "Region", example = "ASIA")
    private Region region;
    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "something")
    private QuestCategory questCategory;
    @Schema(description = "월드 레벨", type = "int", example = "7")
    private int wordLevel;
    @Schema(description = "게시물 내용", type = "String", example = "날 구원해 줄 사람 어디 없나")
    private String content;
    @Schema(description = "비회원 게시물 비밀번호", type = "String", example = "abcd1234!")
    private String password;
    @Schema(description = "게시물 완료 여부", type = "boolean", example = "false")
    private boolean completed;
    @Schema(description = "완료일시", type = "LocalDateTime", example = "2024-08-12 01:12:00")
    private LocalDateTime completedAt;
    @Schema(description = "정렬 기준 시간", type = "LocalDateTime", example = "2024-08-12 01:12:00")
    private LocalDateTime sortedAt;
    @Schema(description = "등록일시", type = "LocalDateTime", example = "2024-08-12 01:12:00")
    private LocalDateTime createdAt;
    @Schema(description = "수정일시 (끌올 / 리스트 순서)", type = "LocalDateTime", example = "2024-08-12 01:12:00")
    private LocalDateTime updatedAt;
}
