package com.next.genshinflow.application.post.mapper;

import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.Region;

import java.time.LocalDateTime;

public class PostMapper {
    private PostMapper() {}

    public static PostEntity toPost(PostCreateRequest request, MemberEntity writer, Region region, LocalDateTime completedAt) {
        long uid = (request.getUid() != 0) ? request.getUid() : writer.getUid();

        PostEntity post = new PostEntity();
        post.setWriter(writer);
        post.setUid(uid);
        post.setRegion(region);
        post.setQuestCategory(request.getQuestCategory());
        post.setWorldLevel(request.getWorldLevel());
        post.setContent(request.getContent());
        post.setPassword(request.getPassword());
        post.setCompletedAt(completedAt);
        post.setSortedAt(LocalDateTime.now());

        return post;
    }

    public static void toUpdatedPost(PostEntity post, PostModifyRequest request) {
        if (request.getUid() != 0) post.setUid(request.getUid());
        if (request.getQuestCategory() != null) post.setQuestCategory(request.getQuestCategory());
        if (request.getWordLevel() != 0) post.setWorldLevel(request.getWordLevel());
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getAutoCompleteTime() >= 30) {
            LocalDateTime completedAt = post.getCreatedAt().plusMinutes(request.getAutoCompleteTime());
            post.setCompletedAt(completedAt);
        }
    }

    public static PostResponse toResponse(PostEntity post) {
        if (post == null) return null;

        String writerEmail = null;
        String writerName = "Guest";

        if (post.getWriter() != null) {
            writerEmail = post.getWriter().getEmail();
            writerName = post.getWriter().getName();
        }

        return PostResponse.builder()
            .id(post.getId())
            .writerEmail(writerEmail)
            .writerName(writerName)
            .uid(post.getUid())
            .region(post.getRegion())
            .questCategory(post.getQuestCategory())
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
