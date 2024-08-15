package com.next.genshinflow.util;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    private PasswordUtils() {
    }

    public static HashedPassword hashWithSalt(int password) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String salt = BCrypt.gensalt();
        String encodedValue = BCrypt.hashpw(String.valueOf(password), salt);

        return new HashedPassword()
            .setSalt(salt)
            .setEncodedPassword(encodedValue);
    }

    public static HashedPassword hashWithSalt(String password) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String salt = BCrypt.gensalt();
        String encodedValue = BCrypt.hashpw(password, salt);

        return new HashedPassword()
            .setSalt(salt)
            .setEncodedPassword(encodedValue);
    }

    public static boolean verifyIfPasswordMatches(
        String password,
        String encodedPassword,
        String salt
    ) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String target = BCrypt.hashpw(password, salt);
        return StringUtils.equals(target, encodedPassword);
    }

    public static boolean verifyIfPasswordMatches(
        int password,
        String encodedPassword,
        String salt
    ) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String target = BCrypt.hashpw(String.valueOf(password), salt);
        return StringUtils.equals(target, encodedPassword);
    }

    private static boolean isNotProperPassword(String password) {
        return StringUtils.isNotBlank(password);
    }

    private static boolean isNotProperPassword(int password) {
        return 0 <= password && password < 10000;
    }
}
