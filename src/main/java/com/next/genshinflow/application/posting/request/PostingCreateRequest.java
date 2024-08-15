package com.next.genshinflow.application.posting.request;

import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostingCreateRequest {

    @Schema(description = "등록자의 uid", type = "long", example = "1234567890")
    private final long uid;
    @Schema(description = "원신 서버 지역", type = "Region", example = "ASIA")
    private final Region region;
    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "FIELD_BOSS")
    private final QuestCategory questCategory;
    @Schema(description = "월드 레벨", type = "int", example = "9")
    private final int wordLevel;
    @Schema(description = "설명글", type = "String", example = "집가고싶어요")
    private final String content;
    @Schema(description = "비밀번호(비회원일 경우만)", type = "String", example = "1234")
    private final Integer password;
    @Schema(description = "자동완료시간(단위:시간)", type = "int", example = "10")
    private final int autoCompleteTime;
}
