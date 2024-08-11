package com.next.genshinflow.enumeration.converter;

import com.next.genshinflow.enumeration.UserStatus;
import jakarta.persistence.Converter;

@Converter
public class ReportStatusConverter extends EnumAttributeConverter<UserStatus> {

    public ReportStatusConverter() {
        super(UserStatus.class);
    }
}
