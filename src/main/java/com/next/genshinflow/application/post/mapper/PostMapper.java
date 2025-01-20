package com.next.genshinflow.application.post.mapper;

import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.Region;
import com.next.genshinflow.infrastructure.enkaApi.UserInfoResponse;

import java.time.LocalDateTime;

public class PostMapper {
    private PostMapper() {}

    public static PostEntity toPost(PostCreateRequest request, MemberEntity writer, Region region, LocalDateTime completedAt) {
        PostEntity post = new PostEntity();
        post.setWriter(writer);
        post.setUid(writer.getUid());
        post.setName(writer.getName());
        post.setRegion(region);
        post.setQuestCategory(request.getQuestCategory());
        post.setWorldLevel(writer.getWorldLevel());
        post.setContent(request.getContent());
        post.setPassword(request.getPassword());
        post.setCompletedAt(completedAt);
        post.setSortedAt(LocalDateTime.now());

        return post;
    }

    public static PostEntity toGuestPost(PostCreateRequest request, UserInfoResponse userInfo, Region region, LocalDateTime completedAt) {
        PostEntity post = new PostEntity();
        post.setWriter(null);
        post.setUid(request.getUid());
        post.setName(userInfo.getPlayerInfo().getNickname() + " (Guest)");
        post.setRegion(region);
        post.setQuestCategory(request.getQuestCategory());
        post.setWorldLevel(userInfo.getPlayerInfo().getWorldLevel());
        post.setContent(request.getContent());
        post.setPassword(request.getPassword());
        post.setCompletedAt(completedAt);
        post.setSortedAt(LocalDateTime.now());

        return post;
    }

    public static void toUpdatedPost(PostEntity post, PostModifyRequest request) {
        if (request.getQuestCategory() != null) post.setQuestCategory(request.getQuestCategory());
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getAutoCompleteTime() >= 30) {
            LocalDateTime completedAt = post.getCreatedAt().plusMinutes(request.getAutoCompleteTime());
            post.setCompletedAt(completedAt);
        }
    }

    public static PostResponse toResponse(PostEntity post) {
        if (post == null) return null;

        String writerEmail = null;
        if (post.getWriter() != null) {
            writerEmail = post.getWriter().getEmail();
        }

        return PostResponse.builder()
            .id(post.getId())
            .writerEmail(writerEmail)
            .writerName(post.getName())
            .uid(post.getUid())
            .region(post.getRegion())
            .questCategory(post.getQuestCategory().getCategory())
            .wordLevel(post.getWorldLevel())
            .content(post.getContent())
            .password(post.getPassword())
            .completed(post.isCompleted())
            .completedAt(post.getCompletedAt())
            .sortedAt(post.getSortedAt())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .build();
    }
}
