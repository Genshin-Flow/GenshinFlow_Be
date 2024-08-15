package com.next.genshinflow.application.posting.request;

import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostingModifyRequest(
    @Schema(description = "변경할 posting 의 id", type = "long", example = "1111")
    long id,
    @Schema(description = "등록자의 uid", type = "long", example = "1234567890")
    long uid,
    @NotNull
    @Schema(description = "원신 서버 지역", type = "Region", example = "ASIA")
    Region region,
    @NotNull
    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "FIELD_BOSS")
    QuestCategory questCategory,

    @Min(value = 1)
    @Max(value = 8)
    @Schema(description = "월드 레벨", type = "int", example = "9")
    int wordLevel,
    @NotBlank
    @Schema(description = "설명글", type = "String", example = "집가고싶어요")
    String content,
    @Schema(description = "비밀번호(비회원일 경우만)", type = "String", example = "1234")
    Integer password,
    @Min(value = 1)
    @Max(value = 10)
    @Schema(description = "자동완료시간(단위:시간)", type = "int", example = "1")
    int autoCompleteTime
) {

}
