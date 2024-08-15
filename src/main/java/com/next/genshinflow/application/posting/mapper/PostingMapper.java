package com.next.genshinflow.application.posting.mapper;

import com.next.genshinflow.application.posting.response.AssignerResponse;
import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.domain.posting.Posting;
import com.next.genshinflow.domain.user.entity.MemberEntity;

public class PostingMapper {

    private PostingMapper() {
    }

    public static PostingResponse mapToResponse(Posting posting, boolean editable) {
        return new PostingResponse(
            posting.getId(),
            mapToAssigner(posting),
            posting.getQuestCategory(),
            posting.getWorldLevel(),
            posting.getUpdatedAt(),
            posting.getCreatedAt(),
            editable
        );
    }

    private static AssignerResponse mapToAssigner(Posting posting) {
        if (posting.getWriter() != null) {
            MemberEntity member = posting.getWriter();
            return new AssignerResponse(
                member.getUid(),
                member.getName(),
                member.getImage(),
                true
            );
        }

        return new AssignerResponse(
            posting.getUid(),
            null,
            null,
            false
        );
    }
}
