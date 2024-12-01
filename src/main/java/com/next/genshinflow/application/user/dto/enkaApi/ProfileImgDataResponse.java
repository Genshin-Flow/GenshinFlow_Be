package com.next.genshinflow.application.user.dto.enkaApi;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 프로필 이미지 json 구조 매핑
@Getter
@NoArgsConstructor
public class ProfileImgDataResponse {
    private int id;
    private String iconPath;
    private int priority;
    private long nameTextMapHash;
    private String KPEKBDJLINF;
    private int unlockParam;
    private long unlockDescTextMapHash;
}
