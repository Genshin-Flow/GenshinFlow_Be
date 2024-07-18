package com.next.genshinflow.domain.repository;

import com.next.genshinflow.domain.entity.DemoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoRepository extends JpaRepository<DemoEntity, Long> {

    Optional<DemoEntity> findById(long id);
}
