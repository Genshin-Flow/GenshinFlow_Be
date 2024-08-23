package com.next.genshinflow.application.posting.mapper;

import com.next.genshinflow.application.posting.request.PostingCreateRequest;
import com.next.genshinflow.application.posting.request.PostingModifyRequest;
import com.next.genshinflow.application.posting.response.AssignerResponse;
import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.domain.posting.Posting;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import java.time.LocalDateTime;

public class PostingMapper {

    private PostingMapper() {
    }

    public static PostingResponse toResponse(Posting posting, boolean editable) {
        return new PostingResponse(
            posting.getId(),
            toAssigner(posting),
            posting.getQuestCategory(),
            posting.getWorldLevel(),
            posting.getUpdatedAt(),
            posting.getCreatedAt(),
            editable
        );
    }

    private static AssignerResponse toAssigner(Posting posting) {
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

    public static Posting from(
        PostingCreateRequest request,
        MemberEntity member,
        String password
    ) {
        return new Posting()
            .setWriter(member)
            .setUid(request.uid())
            .setRegion(request.region())
            .setQuestCategory(request.questCategory())
            .setWorldLevel(request.wordLevel())
            .setContent(request.content())
            .setPassword(password)
            .setCompletedAt(LocalDateTime.now().plusHours(request.autoCompleteTime()));
    }

    public static Posting from(
        PostingModifyRequest request,
        MemberEntity member,
        String password
    ) {
        return new Posting()
            .setWriter(member)
            .setUid(request.uid())
            .setRegion(request.region())
            .setQuestCategory(request.questCategory())
            .setWorldLevel(request.wordLevel())
            .setContent(request.content())
            .setPassword(password)
            .setCompletedAt(LocalDateTime.now().plusHours(request.autoCompleteTime()));
    }
}
