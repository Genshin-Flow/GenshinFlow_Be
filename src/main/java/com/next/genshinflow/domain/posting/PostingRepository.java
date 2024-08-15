package com.next.genshinflow.domain.posting;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface PostingRepository extends JpaRepository<Posting, Long> {

    Page<Posting> findAllByDeletedFalseAndCompletedAtAfterOrderByUpdatedAtDesc(Pageable pageable);

    Optional<Posting> findById(long id);
}
