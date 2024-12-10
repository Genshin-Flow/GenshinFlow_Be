package com.next.genshinflow.application.report.service;

import com.next.genshinflow.application.report.dto.*;
import com.next.genshinflow.application.report.mapper.ReportMapper;
import com.next.genshinflow.application.user.service.AuthService;
import com.next.genshinflow.domain.report.entity.ReportEntity;
import com.next.genshinflow.domain.report.repository.ReportRepository;
import com.next.genshinflow.domain.user.entity.Discipline;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.entity.Warning;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.enumeration.ReportStatus;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReportService {
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final AuthService authService;

    // 신고 생성
    public void createReport(CreateReportRequest reportRequest) {
        if (reportRequest.getReportingUserId().equals(reportRequest.getTargetUserId())) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_REPORT_YOURSELF);
        }

        MemberEntity reportingUser = authService.findMember(reportRequest.getReportingUserId());
        MemberEntity targetUser = authService.findMember(reportRequest.getTargetUserId());

        ReportEntity report = ReportMapper.toReport(reportRequest, reportingUser, targetUser);
        reportRepository.save(report);
    }

    // 모든 신고 내역 조회
    public Page<ReportResponse> getAllReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<ReportEntity> reportPage = reportRepository.findAll(pageable);

        return reportPage.map(ReportMapper::reportToResponse);
    }

    // 상태별 신고 내역 조회
    public Page<ReportResponse> getReportsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        ReportStatus reportStatus = ReportStatus.fromString(status);
        Page<ReportEntity> reportPage = reportRepository.findByReportStatus(reportStatus, pageable);

        return reportPage.map(ReportMapper::reportToResponse);
    }

    // 유저 신고 내역 조회
    public Page<ReportResponse> getUserReportHistory(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<ReportEntity> historyPage = reportRepository.findByTargetUser_Id(userId, pageable);

        return historyPage.map(ReportMapper::reportToResponse);
    }

    public ReportEntity findReport(long reportId) {
        return reportRepository.findById(reportId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.REPORT_NOT_FOUND));
    }

    // ============= 아래에서 부터 패널티 =================

    // 조치 불필요
    public void markReportAsNoActionNeeded(long reportId) {
        ReportEntity report = findReport(reportId);
        report.setReportStatus(ReportStatus.NO_ACTION_NEEDED);
        reportRepository.save(report);
    }

    // 유저 경고
    public void recordUserWarning(UserWarningRequest request) {
        MemberEntity member = authService.findMember(request.getUserId());
        LocalDateTime currentDateTime = LocalDateTime.now();

        Warning action = new Warning(
            request.getReportId(),
            request.getReason(),
            currentDateTime
        );

        member.getWarningHistory().add(action);
        memberRepository.save(member);

        ReportEntity report = findReport(request.getReportId());
        report.setReportStatus(ReportStatus.PROCESSED);
        reportRepository.save(report);
    }

    // 유저 제재
    public void disciplineUser(DisciplinaryActionRequest request) {
        MemberEntity member = authService.findMember(request.getUserId());
        LocalDateTime currentDateTime = LocalDateTime.now();

        Discipline action = new Discipline(
            request.getReportId(),
            request.getDisciplinaryAction(),
            request.getReason(),
            currentDateTime
        );

        member.getDisciplinaryHistory().add(action);
        member.setDisciplineDate(currentDateTime);

        Role status = Arrays.stream(Role.values())
            .filter(accountStatus -> accountStatus.getRole().equals(request.getDisciplinaryAction()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("징계 항목 없음"));

        member.setRole(status);
        memberRepository.save(member);

        ReportEntity report = findReport(request.getReportId());
        report.setReportStatus(ReportStatus.PROCESSED);
        reportRepository.save(report);
    }

    // 유저 경고 해제
    public void unassignUserWarning(RemoveActionRequest request) {
        MemberEntity member = authService.findMember(request.getUserId());
        updateActionHistory(member.getWarningHistory(), request.getReportId(), ExceptionCode.WARNING_NOT_FOUND);
        memberRepository.save(member);
    }

    // 유저 제재 해제
    public void unassignUserDiscipline(RemoveActionRequest request) {
        MemberEntity member = authService.findMember(request.getUserId());
        updateActionHistory(member.getDisciplinaryHistory(), request.getReportId(), ExceptionCode.DISCIPLINE_NOT_FOUND);
        member.setDisciplineDate(null);
        memberRepository.save(member);
    }

    private <T> void updateActionHistory(List<T> history, long reportId, ExceptionCode exceptionCode) {
        boolean removed = history.removeIf(action -> {
            if (action instanceof Warning warning) {
                return warning.getReportId().equals(reportId);
            }
            else if (action instanceof Discipline discipline) {
                return discipline.getReportId().equals(reportId);
            }
            return false;
        });

        if (!removed) {
            throw new BusinessLogicException(exceptionCode);
        }
    }

    // 유저 경고 내역 조회
    public List<Warning> getUserWarningHistory(long userId) {
        MemberEntity member = authService.findMember(userId);
        return member.getWarningHistory();
    }


    // 유저 제재 내역 조회
    public List<Discipline> getUserDisciplineHistory(long userId) {
        MemberEntity member = authService.findMember(userId);
        return member.getDisciplinaryHistory();
    }
}
