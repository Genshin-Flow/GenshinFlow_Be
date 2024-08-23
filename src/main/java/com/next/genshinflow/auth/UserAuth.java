package com.next.genshinflow.auth;

public @interface UserAuth {

    boolean required() default true;
}
