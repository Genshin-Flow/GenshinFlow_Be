package com.next.genshinflow.enumeration.converter;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EnumAttributeConverter<E extends Enum<E>>
    implements AttributeConverter<E, String> {

    private final Class<E> clazz;

    protected EnumAttributeConverter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(E e) {
        return Optional.ofNullable(e)
            .map(Enum::name)
            .orElse(null);
    }

    @Override
    public E convertToEntityAttribute(String dbValue) {
        if (StringUtils.isEmpty(dbValue)) {
            return null;
        }

        try {
            return Enum.valueOf(clazz, dbValue);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgument Exception with Enum {} and value {}", clazz.getSimpleName(),
                dbValue);
            throw e;
        }
    }
}
