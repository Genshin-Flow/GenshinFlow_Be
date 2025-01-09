package com.next.genshinflow.application.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class GuestPostActionRequest {
    @Schema(description = "포스트 Id", type = "long", example = "1")
    @Positive(message = "게시물 Id를 입력해 주세요.")
    private long postId;

    @Schema(description = "비회원 게시물 비밀번호", type = "String", example = "abcd1234!")
    private String password;
}
