package com.next.genshinflow.application.user.dto;

import com.next.genshinflow.enumeration.AccountStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private long id;
    private long uid;
    private String name;
    private String email;
    private String image;
    private AccountStatus status;

//    private Set<AuthorityResponse> role;
    private String role;
}
