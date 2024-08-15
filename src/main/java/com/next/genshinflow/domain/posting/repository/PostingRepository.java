package com.next.genshinflow.domain.posting.repository;

import com.next.genshinflow.domain.posting.entity.PostingEntity;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface PostingRepository extends JpaRepository<PostingEntity, Long> {

}
