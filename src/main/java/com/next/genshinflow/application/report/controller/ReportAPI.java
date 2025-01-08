package com.next.genshinflow.application.report.controller;

import com.next.genshinflow.application.report.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Report", description = "유저 신고 관련 API")
public interface ReportAPI {
    @Operation(summary = "유저 신고")
    ResponseEntity<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest reportRequest);

    @Operation(summary = "신고 내역 전체 조회", description = "size = 15")
    ResponseEntity<List<ReportResponse>> getAllReports(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "15") int size
    );

    @Operation(
        summary = "상태별 신고 내역 전체 조회",
        description = "status = PROCESSED(NO_ACTION_NEEDED), UNPROCESSED / size = 15"
    )
    ResponseEntity<List<ReportResponse>> getReportsByStatus(
        @PathVariable String status,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "15") int size
    );

    @Operation(summary = "유저 신고 내역 조회", description = "size = 20")
    ResponseEntity<List<ReportResponse>> getUserReportHistory(
        @PathVariable String userEmail,
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "20") int size
    );
}
