package com.next.genshinflow.domain.report.entity;

import com.next.genshinflow.domain.BaseEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.ReportStatus;
import com.next.genshinflow.enumeration.converter.ReportStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report")
public class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postingId")
    private Long id;

    @ManyToOne
    @Column
    private MemberEntity reporter;

    @ManyToOne
    @Column
    private MemberEntity reportee;

    @Column
    private String content;

    @Convert(converter = ReportStatusConverter.class)
    @Column
    private ReportStatus status;
}
