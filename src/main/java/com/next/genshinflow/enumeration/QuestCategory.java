package com.next.genshinflow.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum QuestCategory {
    NORMAL_DOMAIN("일반 비경"),
    EVENT_QUEST("이벤트 퀘스트"),
    AREA_CONQUEST("영역 토벌"),
    DAILY_QUEST("일일 임무"),
    MAP_EXPLORATION("맵 탐사"),
    GATHERING("채집");

    private final String category;

    public static QuestCategory fromCategoryName(String categoryName) {
        return Arrays.stream(values())
            .filter(qc -> qc.category.equals(categoryName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "유효하지 않은 퀘스트 종류입니다: " + categoryName + ". 가능한 값: " + Arrays.toString(values())
            ));
    }
}
