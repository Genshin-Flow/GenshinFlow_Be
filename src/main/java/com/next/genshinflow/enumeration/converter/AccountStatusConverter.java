package com.next.genshinflow.enumeration.converter;

import com.next.genshinflow.enumeration.AccountStatus;
import jakarta.persistence.Converter;

@Converter
public class AccountStatusConverter extends EnumAttributeConverter<AccountStatus> {

    public AccountStatusConverter() {
        super(AccountStatus.class);
    }
}
