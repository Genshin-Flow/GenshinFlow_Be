package com.next.genshinflow.application.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PostListResponse extends PostResponse {
    @Schema(description = "게시글 작성자 여부", type = "boolean", example = "false")
    private boolean isWriter;
}
