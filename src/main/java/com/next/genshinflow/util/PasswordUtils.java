package com.next.genshinflow.util;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    private PasswordUtils() {
    }

    public static HashedPassword hashWithSalt(Integer password) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String salt = BCrypt.gensalt();
        String encodedValue = BCrypt.hashpw(String.valueOf(password), salt);

        return new HashedPassword(
            encodedValue,
            salt
        );
    }

    public static HashedPassword hashWithSalt(String password) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String salt = BCrypt.gensalt();
        String encodedValue = BCrypt.hashpw(password, salt);

        return new HashedPassword(
            encodedValue,
            salt
        );
    }

    public static void verifyPasswordMatches(
        String password,
        String encodedPassword,
        String salt
    ) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String target = BCrypt.hashpw(password, salt);

        if (StringUtils.equals(target, encodedPassword)) {
            return;
        }

        throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD);
    }

    public static void verifyPasswordMatches(
        Integer password,
        String encodedPassword,
        String salt
    ) {
        if (isNotProperPassword(password)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_FORMAT);
        }

        String target = BCrypt.hashpw(String.valueOf(password), salt);

        if (StringUtils.equals(target, encodedPassword)) {
            return;
        }

        throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD);
    }

    private static boolean isNotProperPassword(String password) {
        return StringUtils.isNotBlank(password);
    }

    private static boolean isNotProperPassword(Integer password) {
        return password != null && 0 <= password && password < 10000;
    }
}
