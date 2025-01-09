package com.next.genshinflow.application.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class PostCreateRequest {
    @Schema(description = "UID (비회원일 경우만)", type = "long", example = "1234214324")
    private long uid;

    @Schema(description = "퀘스트 종류", type = "QuestCategory", example = "일반 비경, 이벤트 퀘스트 등")
    @NotNull(message = "퀘스트 종류를 입력해 주세요.")
    private QuestCategory questCategory;

    @Schema(description = "월드 레벨", type = "int", example = "6")
    @Min(value = 1, message = "월드 레벨을 입력해 주세요.")
    @Max(value = 8, message = "월드 레벨은 최대 8까지 가능합니다.")
    private int worldLevel;

    @Schema(description = "게시물 내용", type = "String", example = "날 구원해 줄 사람 어디 없나")
    @NotBlank(message = "게시물 내용을 입력해 주세요.")
    private String content;

    @Schema(description = "비회원 게시물 비밀번호 (비회원일 경우만)", type = "String", example = "test1234!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*[0-9])|(?=.*[A-Za-z])(?=.*[!@#$%^&*(),.?\":{}|<>])|(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
        message = "비밀번호는 문자, 숫자, 기호 중 두 가지 이상을 포함하고 8자 이상이어야 합니다."
    )
    private String password;

    @Schema(description = "자동 완료 시간", type = "int", example = "60")
    @Min(value = 30, message = "자동 완료 시간은 30분 이상이어야 합니다.")
    private int autoCompleteTime;
}
