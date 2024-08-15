package com.next.genshinflow.application.posting.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostingDeleteRequest {

    @Schema(description = "끌올할 posting id", type = "long", example = "1234")
    private final long postingId;
    @Schema(description = "비밀번호(비회원일 경우만)", type = "String", example = "1234")
    private final Integer password;
}
