package com.next.genshinflow.application.session;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionController {
    private final SessionManager sessionManager;

    @Operation(summary = "접속한 유저 수, 오늘 구인글 수")
    @GetMapping("/dashboard-status")
    public ResponseEntity<DashboardStatusResponse> getDashboardStats() {
        int activeUsers = sessionManager.getActiveUserCount();
        int todayPosts = sessionManager.getTodayPost();

        DashboardStatusResponse response = new DashboardStatusResponse(activeUsers, todayPosts);
        return ResponseEntity.ok(response);
    }
}
