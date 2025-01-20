package com.next.genshinflow.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.LocalDateTimeAttributeConverter;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import com.next.genshinflow.enumeration.converter.QuestCategoryConverter;
import com.next.genshinflow.enumeration.converter.RegionConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posting")
@Comment("메인 대시보드 post")
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private MemberEntity writer;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "name")
    private String name;

    @Convert(converter = RegionConverter.class)
    @Column(name = "region")
    private Region region;

    @Convert(converter = QuestCategoryConverter.class)
    @Column(name = "quest_category")
    private QuestCategory questCategory;

    @Column(name = "world_level")
    private int worldLevel;

    @Column(name = "content")
    private String content;

    @Column(name = "password")
    private String password;

    @Column(name = "completed", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean completed;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "sorted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sortedAt;
}
