package com.next.genshinflow.enumeration;

import lombok.Getter;

// uid가 9자리면 첫 번째 숫자로
// 10자리면 두 번째 숫자로 구분함
@Getter
public enum Region {
    CHINA, // 1,2,3,4,5
    AMERICA, // 6
    EUROPE, // 7
    ASIA, // 8
    TW_HK_MO, // 9
    NA
}
