package com.next.genshinflow.enumeration.converter;

import com.next.genshinflow.enumeration.QuestCategory;
import jakarta.persistence.Converter;

@Converter
public class QuestCategoryConverter extends EnumAttributeConverter<QuestCategory> {

    public QuestCategoryConverter() {
        super(QuestCategory.class);
    }
}
