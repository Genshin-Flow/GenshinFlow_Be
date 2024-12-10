package com.next.genshinflow.enumeration.converter;

import com.next.genshinflow.enumeration.ReportStatus;
import jakarta.persistence.Converter;

@Converter
public class ReportStatusConverter extends EnumAttributeConverter<ReportStatus> {
    public ReportStatusConverter() {
        super(ReportStatus.class);
    }
}