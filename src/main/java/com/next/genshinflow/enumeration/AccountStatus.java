package com.next.genshinflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatus {
    ACTIVE_USER("활동중인 유저입니다."),
    DORMANT_USER("휴면 유저입니다."),
    DELETED_USER("삭제된 유저입니다."),
    SUSPENDED_FOR_A_WEEK("일주일 정지"),
    PERMANENTLY_BANNED("영구 정지");

    private final String message;

}
