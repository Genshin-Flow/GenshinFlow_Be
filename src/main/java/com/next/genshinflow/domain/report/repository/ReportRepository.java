package com.next.genshinflow.domain.report.repository;

import com.next.genshinflow.domain.report.entity.ReportEntity;
import com.next.genshinflow.enumeration.ReportStatus;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Hidden
@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    Page<ReportEntity> findByReportStatus(ReportStatus reportStatus, Pageable pageable);
    Page<ReportEntity> findByTargetUser_Email(String userEmail, Pageable pageable);
}
