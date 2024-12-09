package com.next.genshinflow.application.report.controller;

import com.next.genshinflow.application.report.dto.*;
import com.next.genshinflow.application.report.service.ReportService;
import com.next.genshinflow.domain.user.entity.Discipline;
import com.next.genshinflow.domain.user.entity.Warning;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Report", description = "유저 신고 관련 API")
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "유저 신고")
    @PostMapping("/user/report")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> createReport(@Valid @RequestBody CreateReportRequest reportRequest) {
        reportService.createReport(reportRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "신고 내역 전체 조회")
    @GetMapping("/report/history")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getAllReports(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "15") int size) {

        Page<ReportResponse> reportPage = reportService.getAllReports(page-1, size);
        return ResponseEntity.ok(reportPage.getContent());
    }

    @Operation(summary = "상태별 신고 내역 전체 조회",description = "status: PROCESSED(NO_ACTION_NEEDED), UNPROCESSED")
    @GetMapping("/report/history/{status}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(
        @PathVariable String status,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "15") int size) {

        Page<ReportResponse> reportPage = reportService.getReportsByStatus(status, page-1, size);
        return ResponseEntity.ok(reportPage.getContent());
    }

    @Operation(summary = "유저 신고 내역 조회")
    @GetMapping("/report/{userId}/history")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getUserReportHistory(@PathVariable long userId,
                                               @Positive @RequestParam(value = "page", defaultValue = "1") int page,
                                               @Positive @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<ReportResponse> reportHistory = reportService.getUserReportHistory(userId, page-1, size);
        return ResponseEntity.ok(reportHistory.getContent());
    }

    // ============= 아래에서 부터 패널티 =================
    @Operation(summary = "조치 불필요")
    @PatchMapping("/report/no-action/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> markReportAsNoActionNeeded(@PathVariable long reportId) {
        reportService.markReportAsNoActionNeeded(reportId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 경고")
    @PatchMapping("/penalty/warning")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> recordUserWarning(@Valid @RequestBody UserWarningRequest request) {
        reportService.recordUserWarning(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 제재")
    @PatchMapping("/penalty/discipline")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> disciplineUser(@Valid @RequestBody DisciplinaryActionRequest request) {
        reportService.disciplineUser(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 경고 해제")
    @PatchMapping("/penalty/warning/delete")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> unassignUserWarning(@Valid @RequestBody RemoveActionRequest request) {
        reportService.unassignUserWarning(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 제재 해제")
    @PatchMapping("/penalty/discipline/delete")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> unassignUserDiscipline(@Valid @RequestBody RemoveActionRequest request) {
        reportService.unassignUserDiscipline(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 경고 내역 확인")
    @GetMapping("/penalty/warning/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Warning>> getUserWarningHistory(@PathVariable long userId) {

        List<Warning> warningHistory = reportService.getUserWarningHistory(userId);
        return ResponseEntity.ok(warningHistory);
    }

    @Operation(summary = "유저 제재 내역 확인")
    @GetMapping("/penalty/discipline/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Discipline>> getUserDisciplineHistory(@PathVariable long userId) {

        List<Discipline> disciplineHistory = reportService.getUserDisciplineHistory(userId);
        return ResponseEntity.ok(disciplineHistory);
    }
}
