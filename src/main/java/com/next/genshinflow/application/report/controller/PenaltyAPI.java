package com.next.genshinflow.application.report.controller;

import com.next.genshinflow.application.report.dto.DisciplinaryActionRequest;
import com.next.genshinflow.application.report.dto.RemoveActionRequest;
import com.next.genshinflow.application.report.dto.UserWarningRequest;
import com.next.genshinflow.domain.user.entity.Discipline;
import com.next.genshinflow.domain.user.entity.Warning;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Penalty", description = "유저 경고, 제재 관련 API")
public interface PenaltyAPI {
    @Operation(summary = "조치 불필요")
    ResponseEntity<Void> markReportAsNoActionNeeded(@PathVariable long reportId);

    @Operation(summary = "유저 경고")
    ResponseEntity<Void> recordUserWarning(@Valid @RequestBody UserWarningRequest request);

    @Operation(summary = "유저 제재")
    ResponseEntity<Void> disciplineUser(@Valid @RequestBody DisciplinaryActionRequest request);

    @Operation(summary = "유저 경고 해제")
    ResponseEntity<Void> unassignUserWarning(@Valid @RequestBody RemoveActionRequest request);

    @Operation(summary = "유저 제재 해제")
    ResponseEntity<Void> unassignUserDiscipline(@Valid @RequestBody RemoveActionRequest request);

    @Operation(summary = "유저 경고 내역 확인")
    ResponseEntity<List<Warning>> getUserWarningHistory(@PathVariable String userEmail);

    @Operation(summary = "유저 제재 내역 확인")
    ResponseEntity<List<Discipline>> getUserDisciplineHistory(@PathVariable String userEmail);
}
