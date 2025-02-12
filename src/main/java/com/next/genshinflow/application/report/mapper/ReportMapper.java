package com.next.genshinflow.application.report.mapper;

import com.next.genshinflow.application.report.dto.CreateReportRequest;
import com.next.genshinflow.application.report.dto.ReportResponse;
import com.next.genshinflow.domain.report.entity.ReportEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.ReportStatus;

public class ReportMapper {
    private ReportMapper() {}

    public static ReportEntity toReport(CreateReportRequest reportRequest, MemberEntity reportingUser, MemberEntity targetUser) {
        ReportEntity report = new ReportEntity();
        report.setReportingUser(reportingUser);
        report.setTargetUser(targetUser);
        report.setReason(reportRequest.getReason());
        report.setImages(reportRequest.getImages());
        report.setReportStatus(ReportStatus.UNPROCESSED);

        return report;
    }

    public static ReportResponse toResponse(ReportEntity report) {
        if (report == null) return null;

        return ReportResponse.builder()
            .id(report.getId())
            .reportingUserEmail(report.getReportingUser().getEmail())
            .targetUserEmail(report.getTargetUser().getEmail())
            .reason(report.getReason())
            .images(report.getImages())
            .reportStatus(report.getReportStatus())
            .createdAt(report.getCreatedAt())
            .completedAt(report.getCompletedAt() != null ? report.getCompletedAt() : null)
            .build();
    }
}
