package com.next.genshinflow.enumeration.converter;

import com.next.genshinflow.enumeration.Region;
import jakarta.persistence.Converter;

@Converter
public class RegionConverter extends EnumAttributeConverter<Region> {

    public RegionConverter() {
        super(Region.class);
    }
}
