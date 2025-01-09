package com.next.genshinflow.application.report.controller;

import com.next.genshinflow.application.report.dto.DisciplinaryActionRequest;
import com.next.genshinflow.application.report.dto.RemoveActionRequest;
import com.next.genshinflow.application.report.dto.UserWarningRequest;
import com.next.genshinflow.application.report.service.PenaltyService;
import com.next.genshinflow.domain.user.entity.Discipline;
import com.next.genshinflow.domain.user.entity.Warning;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PenaltyController implements PenaltyAPI {
    private PenaltyService penaltyService;

    @PatchMapping("/report/no-action/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> markReportAsNoActionNeeded(@PathVariable long reportId) {
        penaltyService.markReportAsNoActionNeeded(reportId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/penalty/warning")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> recordUserWarning(@Valid @RequestBody UserWarningRequest request) {
        penaltyService.recordUserWarning(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/penalty/discipline")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> disciplineUser(@Valid @RequestBody DisciplinaryActionRequest request) {
        penaltyService.disciplineUser(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/penalty/warning/delete")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> unassignUserWarning(@Valid @RequestBody RemoveActionRequest request) {
        penaltyService.unassignUserWarning(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/penalty/discipline/delete")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> unassignUserDiscipline(@Valid @RequestBody RemoveActionRequest request) {
        penaltyService.unassignUserDiscipline(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/penalty/warning/{userEmail}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Warning>> getUserWarningHistory(@PathVariable String userEmail) {

        List<Warning> warningHistory = penaltyService.getUserWarningHistory(userEmail);
        return ResponseEntity.ok(warningHistory);
    }

    @GetMapping("/penalty/discipline/{userEmail}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Discipline>> getUserDisciplineHistory(@PathVariable String userEmail) {

        List<Discipline> disciplineHistory = penaltyService.getUserDisciplineHistory(userEmail);
        return ResponseEntity.ok(disciplineHistory);
    }
}
