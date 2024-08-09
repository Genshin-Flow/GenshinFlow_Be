package com.next.genshinflow.enumeration;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }
}
