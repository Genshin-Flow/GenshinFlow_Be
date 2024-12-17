package com.next.genshinflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN(0,"ROLE_ADMIN"),
    USER(0,"ROLE_USER"),
    SUSPENDED_FOR_A_DAY(1,"1일 정지"),
    SUSPENDED_FOR_A_WEEK(7,"7일 정지"),
    SUSPENDED_FOR_A_MONTH(30,"30일 정지"),
    PERMANENTLY_BANNED(-1,"영구 정지");

    private final int suspensionDays;
    private final String role;

    public boolean isSuspended() {
        return suspensionDays > 0;
    }
}
