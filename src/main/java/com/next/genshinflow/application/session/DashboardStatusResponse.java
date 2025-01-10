package com.next.genshinflow.application.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardStatusResponse {
    @Schema(description = "접속(로그인)한 사용자 수", type = "int", example = "100")
    private int activeUsers;

    @Schema(description = "오늘의 구인글", type = "int", example = "100")
    private int todayPost;
}
