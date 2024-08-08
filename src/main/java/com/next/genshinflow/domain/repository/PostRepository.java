package com.next.genshinflow.domain.repository;

import com.next.genshinflow.domain.entity.PostEntity;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Hidden
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
