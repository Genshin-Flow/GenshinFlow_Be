package com.next.genshinflow.application.posting.request;

import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostingModifyRequest(
    @Schema(description = "원신 서버 지역", type = "Region", example = "ASIA")
    Region region,
    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "FIELD_BOSS")
    QuestCategory questCategory,
    @Schema(description = "월드 레벨", type = "int", example = "9")
    Integer wordLevel,
    @Schema(description = "설명글", type = "String", example = "집가고싶어요")
    String content,
    @Schema(description = "비밀번호(비회원일 경우만)", type = "String", example = "1234")
    Integer password,
    @Schema(description = "자동완료시간(단위:시간)", type = "int", example = "10")
    Integer autoCompleteTime
) {

}
