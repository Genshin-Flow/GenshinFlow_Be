package com.next.genshinflow.application.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageRequest {
    @NotBlank(message = "UID는 필수 입력 사항입니다.")
    private int uid;

    private String name;
}
