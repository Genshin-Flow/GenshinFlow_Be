package com.next.genshinflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
// todo: 숫자(int)를 추가하여 role 검증 로직이 문자열이 아닌 숫자로 검증하도록 수정해야함
// todo: 테스트 용도로 10초 정지 만들어야함
// 현재 문자열 혹은 숫자로 role을 검증 하더라도 Role이 추가되거나 수정되었을때 role 검증 로직 수정이 필요한지 확인 요망
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
