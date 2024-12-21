package com.next.genshinflow.application.post.mapper;

import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.user.entity.MemberEntity;

import java.time.LocalDateTime;

public class PostMapper {
    private PostMapper() {}

    public static PostEntity toPost(PostCreateRequest request, MemberEntity writer, LocalDateTime completedAt) {
        long uid = (request.getUid() != 0) ? request.getUid() : writer.getUid();

        PostEntity post = new PostEntity();
        post.setWriter(writer);
        post.setTitle(request.getTitle());
        post.setUid(uid);
        post.setRegion(request.getRegion());
        post.setQuestCategory(request.getQuestCategory());
        post.setWorldLevel(request.getWorldLevel());
        post.setContent(request.getContent());
        post.setPassword(request.getPassword());
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
        String writerEmail = (post.getWriter() != null) ? post.getWriter().getEmail() : null;

        return PostResponse.builder()
            .id(post.getId())
            .writerEmail(writerEmail)
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
