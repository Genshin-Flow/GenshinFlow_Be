package com.next.genshinflow.domain.entity;

import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.LocalDateTimeAttributeConverter;
import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "post")
@Comment("메인 대시보드 post")
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @Column
    private MemberEntity writer;

    @Column(length = 20)
    private String title;

    @Column
    private Long uid;

    @Column
    private Region region;

    @Column
    private QuestCategory questCategory;

    @Column(nullable = false)
    private int worldLevel;

    @Column
    private String content;

    @Column
    @Comment("writer 가 없을 경우 사용하는 password 해시값")
    private String password;

    @Column
    @Comment("password hash 생성 시 사용한 salt 값")
    private String salt;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean deleted;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Comment("요청글인지 여부")
    private boolean request;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column
    private LocalDateTime completedAt;
}
