package com.next.genshinflow.application.post.service;

import com.next.genshinflow.application.post.dto.GuestPostActionRequest;
import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.application.post.mapper.PostMapper;
import com.next.genshinflow.application.user.service.AuthService;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.post.repository.PostRepository;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final AuthService authService;
    private final PostRepository postRepository;

    public PostResponse createUserPost(PostCreateRequest request) {
        MemberEntity member = authService.getCurrentMember();
        return savePost(request, member);
    }

    public PostResponse createGuestPost(PostCreateRequest request) {
        return savePost(request, null);
    }

    private PostResponse savePost(PostCreateRequest request, MemberEntity member) {
        LocalDateTime completedAt = LocalDateTime.now().plusMinutes(request.getAutoCompleteTime());

        PostEntity post = (member != null)
            ? PostMapper.toPost(request, member, completedAt)
            : PostMapper.toGuestPost(request, completedAt);

        PostEntity savedPost = postRepository.save(post);
        return PostMapper.toResponse(savedPost);
    }

    // 게시물 전체 조회
    public Page<PostResponse> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortedAt").descending());
        Page<PostEntity> postPage = postRepository.findAll(pageable);

        // 게시물 자동 완료 처리
        autoCompletedPost(pageable);

        final Long member = (authService.getCurrentMember() != null) ? authService.getCurrentMember().getId() : null;

        if (member == null) {
            return postPage.map(PostMapper::toResponse);
        }

        return postPage.map(post -> PostMapper.toListResponse(post, member));
    }

    // 게시물 완료 처리
    public PostResponse completeUserPost(long postId) {
        return completePost(validatePost(postId, null));
    }

    // 비회원 게시물 완료 처리
    public PostResponse completeGuestPost(GuestPostActionRequest request) {
        return completePost(validatePost(request.getPostId(), request.getPassword()));
    }

    private PostResponse completePost(PostEntity post) {
        post.setCompleted(true);
        return PostMapper.toResponse(postRepository.save(post));
    }

    // 게시물 자동 완료 처리
    public void autoCompletedPost(Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();
        Page<PostEntity> postsToUpdate = postRepository.findByCompletedFalse(pageable);

        List<PostEntity> postsToSave = new ArrayList<>();

        for (PostEntity post : postsToUpdate) {
            if (post.getCompletedAt().isBefore(currentTime)) {
                post.setCompleted(true);
                postsToSave.add(post);
            }
        }

        if (!postsToSave.isEmpty()) {
            postRepository.saveAll(postsToSave);
        }
    }

    // 게시물 수정
    public PostResponse modifyUserPost(PostModifyRequest request) {
        return modifyPost(validatePost(request.getPostId(), null), request);
    }

    // 비회원 게시물 수정
    public PostResponse modifyGuestPost(PostModifyRequest request) {
        PostEntity post = validatePost(request.getPostId(), request.getPassword());
        return modifyPost(post, request);
    }

    private PostResponse modifyPost(PostEntity post, PostModifyRequest request) {
        PostMapper.toUpdatedPost(post, request);
        postRepository.save(post);
        return PostMapper.toResponse(post);
    }

    // 끌올
    public PostResponse pullUpUserPost(long postId) {
        return pullUpPost(validatePost(postId, null));
    }

    // 비회원 끌올
    public PostResponse pullUpGuestPost(GuestPostActionRequest request) {
        return pullUpPost(validatePost(request.getPostId(), request.getPassword()));
    }

    private PostResponse pullUpPost(PostEntity post) {
        post.setSortedAt(LocalDateTime.now());
        postRepository.save(post);
        return PostMapper.toResponse(post);
    }

    public void deleteUserPost(long postId) {
        deletePost(validatePost(postId, null));
    }

    public void deleteGuestPost(GuestPostActionRequest request) {
        deletePost(validatePost(request.getPostId(), request.getPassword()));
    }

    private void deletePost(PostEntity post) {
        try {
            postRepository.delete(post);
        }
        catch (EmptyResultDataAccessException e) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
    }

    private PostEntity validatePost(long postId, String password) {
        PostEntity post = findPost(postId);

        if (password == null) {
            MemberEntity member = authService.getCurrentMember();

            if (!post.getWriter().getId().equals(member.getId())) {
                throw new BusinessLogicException(ExceptionCode.NO_PERMISSION);
            }

            return post;
        }
        else {
            if (!post.getPassword().equals(password)) {
                throw new BusinessLogicException(ExceptionCode.INVALID_POST_PASSWORD);
            }

            return post;
        }
    }

    public PostEntity findPost(long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
    }
}
