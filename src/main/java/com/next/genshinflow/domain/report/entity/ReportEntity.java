package com.next.genshinflow.domain.report.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.ReportStatus;
import com.next.genshinflow.enumeration.converter.ReportStatusConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "image")
    private String image;

    @Convert(converter = ReportStatusConverter.class)
    @Column(name = "report_status")
    private ReportStatus reportStatus;

    @Column(name = "completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
}
