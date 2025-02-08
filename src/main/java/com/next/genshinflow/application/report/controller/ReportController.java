package com.next.genshinflow.application.report.controller;

import com.next.genshinflow.application.BasePageResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController implements ReportAPI {
    private final ReportService reportService;

    @PostMapping("/user/report")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest reportRequest) {
        ReportResponse reportResponse = reportService.createReport(reportRequest);
        return ResponseEntity.status(HttpStatus.OK).body(reportResponse);
    }

    @GetMapping("/report/history")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BasePageResponse<ReportResponse>> getAllReports(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        BasePageResponse<ReportResponse> reportHistory = reportService.getAllReports(page-1, size);
        return ResponseEntity.ok(reportHistory);
    }

    @GetMapping("/report/history/{status}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BasePageResponse<ReportResponse>> getReportsByStatus(
        @PathVariable String status,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        BasePageResponse<ReportResponse> reportHistory = reportService.getReportsByStatus(status, page-1, size);
        return ResponseEntity.ok(reportHistory);
    }

    @GetMapping("/report/{userEmail}/history")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BasePageResponse<ReportResponse>> getUserReportHistory(
        @PathVariable String userEmail,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        BasePageResponse<ReportResponse> reportHistory = reportService.getUserReportHistory(userEmail, page-1, size);
        return ResponseEntity.ok(reportHistory);
    }
}
