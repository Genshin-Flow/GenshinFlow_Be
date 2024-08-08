package com.next.genshinflow.domain.repository;

import com.next.genshinflow.domain.entity.ReportEntity;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

}
