package com.next.genshinflow.domain.entity;

import com.next.genshinflow.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "demo")
@Schema(description = "Entity swagger 노출 필요시 작성")
@Comment("테스트 데모를 위한 엔티티")
public class DemoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Comment("데모 ID")
    private long id;
}
