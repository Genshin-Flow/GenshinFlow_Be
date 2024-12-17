package com.next.genshinflow.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PROCESSED("처리됨"),
    UNPROCESSED("처리되지 않음"),
    NO_ACTION_NEEDED("조치 불필요");

    private final String message;

    public static ReportStatus fromString(String status) {
        return Arrays.stream(values())
            .filter(s -> s.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("신고 처리 항목 없음: " + status));
    }
}
