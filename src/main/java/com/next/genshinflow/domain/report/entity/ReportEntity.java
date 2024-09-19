package com.next.genshinflow.domain.report.entity;

import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import jakarta.persistence.*;

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

//    @Convert(converter = ReportStatusConverter.class)
    @Column
    private ReportEntity status;
}
