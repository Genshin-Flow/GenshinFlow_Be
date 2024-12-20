package com.next.genshinflow.domain.report.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.ReportStatus;
import com.next.genshinflow.enumeration.converter.ReportStatusConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// 현재 제재 상태인 유저는 비회원으로 게시글 작성이 가능함
// 추후에 유저의 ip를 차단하는 방식으로 변경해야함

@Entity
@Getter
@Setter
@Table(name = "report")
@AllArgsConstructor
@NoArgsConstructor
public class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_user")
    private MemberEntity reportingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user")
    private MemberEntity targetUser;

    @Column(name = "content")
    private String content;

    @ElementCollection
    @CollectionTable(name = "report_images", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "image")
    private List<String> images;

    @Convert(converter = ReportStatusConverter.class)
    @Column(name = "report_status")
    private ReportStatus reportStatus;

    @Column(name = "completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
}
