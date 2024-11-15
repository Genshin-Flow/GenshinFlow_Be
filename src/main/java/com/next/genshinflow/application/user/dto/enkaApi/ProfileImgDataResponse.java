package com.next.genshinflow.application.user.dto.enkaApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 프로필 이미지 json 구조 매핑
@Getter
@Setter
@AllArgsConstructor
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
