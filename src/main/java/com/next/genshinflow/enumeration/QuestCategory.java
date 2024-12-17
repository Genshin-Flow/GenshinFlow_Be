package com.next.genshinflow.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
