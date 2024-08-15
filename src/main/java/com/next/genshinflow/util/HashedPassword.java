package com.next.genshinflow.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class HashedPassword {

    private String encodedPassword;
    private String salt;
}
