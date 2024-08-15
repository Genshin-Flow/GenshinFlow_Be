package com.next.genshinflow.util;

public record HashedPassword(
    String encodedPassword,
    String salt
) {

    public static HashedPassword EMPTY = new HashedPassword(null, null);
}
