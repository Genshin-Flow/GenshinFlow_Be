package com.next.genshinflow.application.post.mapper;

import com.next.genshinflow.application.post.dto.PostListResponse;
import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;

import java.time.LocalDateTime;

public class PostMapper {
    private PostMapper() {}

    public static PostEntity toPost(PostCreateRequest request, MemberEntity writer, LocalDateTime completedAt) {
        PostEntity post = toPostBase(request, writer, completedAt);
        post.setUid(writer.getUid());
        return post;
    }

    public static PostEntity toGuestPost(PostCreateRequest request, LocalDateTime completedAt) {
        PostEntity post = toPostBase(request, null, completedAt);
        post.setUid(request.getUid());
        post.setPassword(request.getPassword());
        return post;
    }

    private static PostEntity toPostBase(PostCreateRequest request, MemberEntity writer, LocalDateTime completedAt) {
        PostEntity post = new PostEntity();
        post.setWriter(writer);
        post.setTitle(request.getTitle());
        post.setRegion(request.getRegion());
        post.setQuestCategory(request.getQuestCategory());
        post.setWorldLevel(request.getWorldLevel());
        post.setContent(request.getContent());
        post.setCompletedAt(completedAt);
        post.setSortedAt(LocalDateTime.now());

        return post;
    }

    public static void toUpdatedPost(PostEntity post, PostModifyRequest request) {
        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getUid() != 0) post.setUid(request.getUid());
        if (request.getRegion() != null) post.setRegion(request.getRegion());
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

        return PostResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
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

    public static PostListResponse toListResponse(PostEntity post, Long currentUserId) {
        if (post == null) return null;

        return PostListResponse.builder()
            .id(post.getId())
            .isWriter(post.getWriter() != null && post.getWriter().getId().equals(currentUserId))
            .title(post.getTitle())
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
