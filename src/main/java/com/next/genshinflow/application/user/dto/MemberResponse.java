package com.next.genshinflow.application.user.dto;

import com.next.genshinflow.enumeration.AccountStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private long id;
    private long uid;
    private String name;
    private String email;
    private String image;
    private int level;
    private int worldLevel;
    private String towerLevel;
    private AccountStatus status;
    private String role;
}
