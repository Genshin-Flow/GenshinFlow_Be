package com.next.genshinflow.domain.post.entity;

import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.LocalDateTimeAttributeConverter;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import com.next.genshinflow.enumeration.converter.QuestCategoryConverter;
import com.next.genshinflow.enumeration.converter.RegionConverter;
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

    @Convert(converter = RegionConverter.class)
    @Column
    private Region region;

    @Convert(converter = QuestCategoryConverter.class)
    @Column
    private QuestCategory questCategory;

    @Column(nullable = false)
    private int worldLevel;

    @Column
    private String content;

    @Column
    private String password;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean deleted;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column
    private LocalDateTime completedAt;
}
