package com.next.genshinflow.enumeration.converter;

import com.next.genshinflow.enumeration.Role;
import jakarta.persistence.Converter;

@Converter
public class RoleConverter extends EnumAttributeConverter<Role> {

    public RoleConverter() {
        super(Role.class);
    }
}
