package com.next.genshinflow.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Type;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@Profile(Phase.TEST)
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        tableNames = entityManager.getMetamodel().getEntities().stream()
            .map(Type::getJavaType)
            .filter(it -> Objects.nonNull(it.getAnnotation(Entity.class)))
            .map(it -> it.getAnnotation(Table.class).name())
            .toList();
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        tableNames.forEach(it -> {
            entityManager.createNativeQuery("TRUNCATE TABLE " + it).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + it + " ALTER COLUMN ID RESTART WITH 1")
                .executeUpdate();
        });
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
