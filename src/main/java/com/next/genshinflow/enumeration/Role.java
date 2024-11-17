package com.next.genshinflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    OAUTH_USER("ROLE_OAUTH_USER");

    private final String role;
}
