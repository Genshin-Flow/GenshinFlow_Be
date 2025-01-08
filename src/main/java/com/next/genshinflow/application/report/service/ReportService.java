package com.next.genshinflow.application.report.service;

import com.next.genshinflow.application.report.dto.*;
import com.next.genshinflow.application.report.mapper.ReportMapper;
import com.next.genshinflow.application.EntityFinder;
import com.next.genshinflow.application.validation.ReportValidationManager;
import com.next.genshinflow.domain.report.entity.ReportEntity;
import com.next.genshinflow.domain.report.repository.ReportRepository;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.ReportStatus;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReportService {
    private final EntityFinder entityFinder;
    private final ReportRepository reportRepository;
    private final ReportValidationManager validationManager;

    // 신고 생성
    public ReportResponse createReport(CreateReportRequest reportRequest) {
        MemberEntity member = entityFinder.getCurrentMember();
        validationManager.validateNotSelfReport(member.getEmail(), reportRequest.getTargetUserEmail());

        MemberEntity targetUser = entityFinder.findMemberByEmail(reportRequest.getTargetUserEmail());

        ReportEntity report = ReportMapper.toReport(reportRequest, member, targetUser);
        ReportEntity createReport = reportRepository.save(report);
        return ReportMapper.toResponse(createReport);
    }

    // 모든 신고 내역 조회
    public Page<ReportResponse> getAllReports(int page, int size) {
        Pageable pageable = createPageable(page, size);
        Page<ReportEntity> historyPage = reportRepository.findAll(pageable);

        return historyPage.map(ReportMapper::toResponse);
    }

    // 상태별 신고 내역 조회
    public Page<ReportResponse> getReportsByStatus(String status, int page, int size) {
        Pageable pageable = createPageable(page, size);

        ReportStatus reportStatus = ReportStatus.fromString(status);
        Page<ReportEntity> historyPage = reportRepository.findByReportStatus(reportStatus, pageable);

        return historyPage.map(ReportMapper::toResponse);
    }

    // 유저 신고 내역 조회
    public Page<ReportResponse> getUserReportHistory(String userEmail, int page, int size) {
        Pageable pageable = createPageable(page, size);
        Page<ReportEntity> historyPage = reportRepository.findByTargetUser_Email(userEmail, pageable);

        return historyPage.map(ReportMapper::toResponse);
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("createdAt").ascending());
    }
}
