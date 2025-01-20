package com.next.genshinflow.application.post.service;

import com.next.genshinflow.application.BasePageResponse;
import com.next.genshinflow.application.post.dto.GuestPostActionRequest;
import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.application.post.mapper.PostMapper;
import com.next.genshinflow.application.EntityFinder;
import com.next.genshinflow.application.validation.PostValidationManager;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.post.repository.PostRepository;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.Region;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.infrastructure.enkaApi.EnkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final EntityFinder entityFinder;
    private final PostRepository postRepository;
    private final PostValidationManager validationManager;
    private final EnkaService enkaService;

    public PostResponse createUserPost(PostCreateRequest request) {
        MemberEntity member = entityFinder.getCurrentMember();
        return savePost(request, member);
    }

    public PostResponse createGuestPost(PostCreateRequest request) {
        validationManager.validateGuestPost(request.getUid(), request.getPassword());
        return savePost(request, null);
    }

    private PostResponse savePost(PostCreateRequest request, MemberEntity member) {
        Region region = (member != null) ? determineRegion(member.getUid()) : determineRegion(request.getUid());
        LocalDateTime completedAt = LocalDateTime.now().plusMinutes(request.getAutoCompleteTime());

        PostEntity post = (member != null)
            ? PostMapper.toPost(request, member, region, completedAt)
            : PostMapper.toGuestPost(request, enkaService.getUserInfoFromApi(request.getUid()), region, completedAt);

        PostEntity savedPost = postRepository.save(post);
        return PostMapper.toResponse(savedPost);
    }

    private Region determineRegion(long uid) {
        String uidString = Long.toString(uid);

        if (uidString.length() != 9 && uidString.length() != 10) {
            return Region.NA;
        }

        int serverNum = uidString.length() == 9
            ? Character.getNumericValue(uidString.charAt(0))
            : Character.getNumericValue(uidString.charAt(1));

        return switch (serverNum) {
            case 1, 2, 3, 4, 5 -> Region.CHINA;
            case 6 -> Region.AMERICA;
            case 7 -> Region.EUROPE;
            case 8 -> Region.ASIA;
            case 9 -> Region.TW_HK_MO;
            default -> Region.NA;
        };
    }

    // 게시물 전체 조회
    public BasePageResponse<PostResponse> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortedAt").descending());
        Page<PostEntity> postPage = postRepository.findAll(pageable);

        // 게시물 자동 완료 처리
        autoCompletedPost(pageable);

        Page<PostResponse> postResponsePage = postPage.map(PostMapper::toResponse);
        return new BasePageResponse<>(
            postResponsePage.getContent(),
            postResponsePage.getNumber() + 1,
            postResponsePage.getSize(),
            postResponsePage.getTotalElements(),
            postResponsePage.getTotalPages()
        );
    }

    public BasePageResponse<PostResponse> getFilteredPosts(
        int page,
        int size,
        List<Region> regions,
        List<String> questCategories,
        List<Integer> worldLevels
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortedAt").descending());

        Specification<PostEntity> spec = Specification.where(PostSpecification.filterByRegions(regions))
            .and(PostSpecification.filterByQuestCategories(questCategories))
            .and(PostSpecification.filterByWorldLevels(worldLevels));

        Page<PostEntity> postPage = postRepository.findAll(spec, pageable);

        // 게시물 자동 완료 처리
        autoCompletedPost(pageable);

        Page<PostResponse> postResponsePage = postPage.map(PostMapper::toResponse);
        return new BasePageResponse<>(
            postResponsePage.getContent(),
            postResponsePage.getNumber() + 1,
            postResponsePage.getSize(),
            postResponsePage.getTotalElements(),
            postResponsePage.getTotalPages()
        );
    }

    // 게시물 완료 처리
    public void completeUserPost(long postId) {
        completePost(validationManager.authorizePostAccess(postId, null));
    }

    // 비회원 게시물 완료 처리
    public void completeGuestPost(GuestPostActionRequest request) {
        completePost(validationManager.authorizePostAccess(request.getPostId(), request.getPassword()));
    }

    private void completePost(PostEntity post) {
        post.setCompleted(true);
        postRepository.save(post);
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

        if (!postsToSave.isEmpty()) postRepository.saveAll(postsToSave);
    }

    // 게시물 수정
    public PostResponse modifyUserPost(PostModifyRequest request) {
        return modifyPost(validationManager.authorizePostAccess(request.getPostId(), null), request);
    }

    // 비회원 게시물 수정
    public PostResponse modifyGuestPost(PostModifyRequest request) {
        PostEntity post = validationManager.authorizePostAccess(request.getPostId(), request.getPassword());
        return modifyPost(post, request);
    }

    private PostResponse modifyPost(PostEntity post, PostModifyRequest request) {
        PostMapper.toUpdatedPost(post, request);
        postRepository.save(post);
        return PostMapper.toResponse(post);
    }

    // 끌올
    public void pullUpUserPost(long postId) {
        pullUpPost(validationManager.authorizePostAccess(postId, null));
    }

    // 비회원 끌올
    public void pullUpGuestPost(GuestPostActionRequest request) {
        pullUpPost(validationManager.authorizePostAccess(request.getPostId(), request.getPassword()));
    }

    private void pullUpPost(PostEntity post) {
        post.setSortedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public void deleteUserPost(long postId) {
        deletePost(validationManager.authorizePostAccess(postId, null));
    }

    public void deleteGuestPost(GuestPostActionRequest request) {
        deletePost(validationManager.authorizePostAccess(request.getPostId(), request.getPassword()));
    }

    private void deletePost(PostEntity post) {
        try {
            postRepository.delete(post);
        }
        catch (EmptyResultDataAccessException e) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
    }
}
