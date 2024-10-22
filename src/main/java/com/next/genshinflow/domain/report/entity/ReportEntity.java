package com.next.genshinflow.domain.report.entity;

import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.ReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report")
public class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private MemberEntity reporter;

    @ManyToOne
    @JoinColumn(name = "reportee_id")
    private MemberEntity reportee;

    @Column
    private String content;

    @Column
    private ReportStatus status;
}
