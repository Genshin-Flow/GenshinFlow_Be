package com.next.genshinflow.application.posting.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssignerResponse(
    @Schema(description = "유저 id", type = "long", example = "1")
    long uid,
    @Schema(description = "닉네임", type = "String", example = "exampleName")
    String nickName,
    @Schema(description = "프로필 이미지(수정 예정)", type = "String", example = "example.jpg")
    String profileImage,
    @Schema(description = "회원인가의 여부", type = "boolean", example = "true")
    boolean isMember
) {

}
