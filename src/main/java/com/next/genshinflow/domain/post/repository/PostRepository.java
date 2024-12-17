package com.next.genshinflow.domain.post.repository;

import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.report.entity.ReportEntity;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Hidden
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findByWriter_Id(long writerId, Pageable pageable);
    Page<PostEntity> findByCompletedFalse(Pageable pageable);
}
