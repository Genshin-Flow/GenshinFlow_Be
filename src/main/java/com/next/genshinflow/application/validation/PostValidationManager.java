package com.next.genshinflow.application.validation;

import com.next.genshinflow.application.EntityFinder;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidationManager {
    private final EntityFinder entityFinder;

    public void validateGuestPost(long uid, String password) {
        validateGuestUid(uid);
        validateGuestPassword(password);
    }

    public PostEntity authorizePostAccess(long postId, String password) {
        PostEntity post = entityFinder.findPost(postId);

        if (password == null) {
            MemberEntity member = entityFinder.getCurrentMember();

            if (!post.getWriter().getId().equals(member.getId())) {
                log.warn("Access denied for userId: {} on postId: {}", member.getId(), postId);
                throw new BusinessLogicException(ExceptionCode.NO_PERMISSION);
            }
        }
        else {
            if (!post.getPassword().equals(password)) {
                log.warn("Invalid password for postId: {}", postId);
                throw new BusinessLogicException(ExceptionCode.INVALID_POST_PASSWORD);
            }
        }
        return post;
    }

    private void validateGuestUid(long uid) {
        if (uid <= 0) {
            log.error("Invalid UID: {}", uid);
            throw new BusinessLogicException(ExceptionCode.UID_REQUIRED_FOR_GUEST);
        }
    }

    private void validateGuestPassword(String password) {
        if (password == null || password.isBlank()) {
            log.error("Password validation failed: password is null or blank");
            throw new BusinessLogicException(ExceptionCode.PASSWORD_REQUIRED_FOR_GUEST);
        }
    }
}
