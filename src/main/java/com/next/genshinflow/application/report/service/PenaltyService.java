package com.next.genshinflow.application.report.service;

import com.next.genshinflow.application.report.dto.DisciplinaryActionRequest;
import com.next.genshinflow.application.report.dto.RemoveActionRequest;
import com.next.genshinflow.application.report.dto.UserWarningRequest;
import com.next.genshinflow.application.EntityFinder;
import com.next.genshinflow.application.validation.ReportValidationManager;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PenaltyService {
    private final EntityFinder entityFinder;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final ReportValidationManager validationManager;
    private static final Map<String, Role> ROLE_MAP = Arrays.stream(Role.values())
        .collect(Collectors.toMap(Role::getRole, Function.identity()));

    // 조치 불필요
    public void markReportAsNoActionNeeded(long reportId) {
        ReportEntity report = entityFinder.findReport(reportId);
        report.setReportStatus(ReportStatus.NO_ACTION_NEEDED);
        reportRepository.save(report);
    }

    // 유저 경고
    @Transactional
    public void recordUserWarning(UserWarningRequest request) {
        MemberEntity member = entityFinder.findMemberByEmail(request.getUserEmail());
        LocalDateTime currentDateTime = LocalDateTime.now();

        Warning action = new Warning(
            request.getReportId(),
            request.getReason(),
            currentDateTime
        );

        member.getWarningHistory().add(action);
        memberRepository.save(member);

        ReportEntity report = entityFinder.findReport(request.getReportId());
        report.setReportStatus(ReportStatus.PROCESSED);
        reportRepository.save(report);
    }

    // 유저 제재
    // todo: 공격자가 Admin으로 제재를 적용할 가능성 있음 수정 요망
    @Transactional
    public void disciplineUser(DisciplinaryActionRequest request) {
        MemberEntity member = entityFinder.findMemberByEmail(request.getUserEmail());
        LocalDateTime currentDateTime = LocalDateTime.now();

        Discipline action = new Discipline(
            request.getReportId(),
            request.getDisciplinaryAction(),
            request.getReason(),
            currentDateTime
        );

        member.getDisciplinaryHistory().add(action);
        member.setDisciplineDate(currentDateTime);

        Role status = Optional.ofNullable(ROLE_MAP.get(request.getDisciplinaryAction()))
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_DISCIPLINARY_ACTION));

        member.setRole(status);
        memberRepository.save(member);

        ReportEntity report = entityFinder.findReport(request.getReportId());
        report.setReportStatus(ReportStatus.PROCESSED);
        reportRepository.save(report);
    }

    // 유저 경고 해제
    public void unassignUserWarning(RemoveActionRequest request) {
        MemberEntity member = entityFinder.findMemberByEmail(request.getUserEmail());
        updateActionHistory(member.getWarningHistory(), request.getReportId(), ExceptionCode.WARNING_NOT_FOUND);
        memberRepository.save(member);
    }

    // 유저 제재 해제
    public void unassignUserDiscipline(RemoveActionRequest request) {
        MemberEntity member = entityFinder.findMemberByEmail(request.getUserEmail());
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

        if (!removed) throw new BusinessLogicException(exceptionCode);
    }

    // 유저 경고 내역 조회
    public List<Warning> getUserWarningHistory(String userEmail) {
        MemberEntity member = entityFinder.findMemberByEmail(userEmail);
        return member.getWarningHistory();
    }

    // 유저 제재 내역 조회
    public List<Discipline> getUserDisciplineHistory(String userEmail) {
        MemberEntity member = entityFinder.findMemberByEmail(userEmail);
        return member.getDisciplinaryHistory();
    }
}
