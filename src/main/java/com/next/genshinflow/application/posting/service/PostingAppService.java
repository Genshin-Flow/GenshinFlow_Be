package com.next.genshinflow.application.posting.service;

import com.next.genshinflow.application.PageResponse;
import com.next.genshinflow.application.posting.mapper.PostingMapper;
import com.next.genshinflow.application.posting.request.PostingCreateRequest;
import com.next.genshinflow.application.posting.request.PostingDeleteRequest;
import com.next.genshinflow.application.posting.request.PostingModifyRequest;
import com.next.genshinflow.application.posting.request.PostingPullUpRequest;
import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.application.user.dto.MemberResponse;
import com.next.genshinflow.domain.posting.Posting;
import com.next.genshinflow.domain.posting.PostingService;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostingAppService {

    private final PostingService postingService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public PostingResponse getPosting(long postingId) {
        Posting posting = postingService.getPostingById(postingId);
        return PostingMapper.toResponse(posting, false);
    }

    public PageResponse<PostingResponse> getPostings(MemberResponse member, Pageable pageable) {
        Page<Posting> postingPage = postingService.getDashboardPostings(pageable);
        List<PostingResponse> postingList = postingPage.getContent().stream()
            .map(it -> PostingMapper.toResponse(it, this.judgeEditable(member, it)))
            .toList();

        return PageResponse.of(
            postingList, postingPage.getTotalElements(), postingPage.getTotalPages());
    }

    private boolean judgeEditable(MemberResponse member, Posting target) {
        return member != null && target.getUid() == member.getUid();
    }

    @Transactional
    public PostingResponse createNonMemberPosting(PostingCreateRequest request) {
        String password = passwordEncoder.encode(request.password().toString());
        Posting posting = PostingMapper.from(request, null, password);
        Posting savedPosting = postingService.createPosting(posting);

        return PostingMapper.toResponse(savedPosting, false);
    }

    @Transactional
    public PostingResponse createMemberPosting(
        PostingCreateRequest request,
        MemberResponse member
    ) {
        MemberEntity writer = memberRepository.findById(member.getId())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Posting posting = PostingMapper.from(request, writer, null);
        Posting savedPosting = postingService.createPosting(posting);

        return PostingMapper.toResponse(savedPosting, true);
    }

    @Transactional
    public PostingResponse modifyNonMemberPosting(
        PostingModifyRequest request
    ) {
        String hashedPassword = postingService.getPostingById(request.postingId()).getPassword();
        passwordEncoder.matches(request.password().toString(), hashedPassword);

        Posting posting = PostingMapper.from(request, null, hashedPassword);
        Posting savedPosting = postingService.modifyPosting(posting);

        return PostingMapper.toResponse(savedPosting, false);
    }

    @Transactional
    public PostingResponse modifyMemberPosting(
        PostingModifyRequest request,
        MemberResponse member
    ) {
        MemberEntity writer = memberRepository.findById(member.getId())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Posting posting = PostingMapper.from(request, writer, null);
        Posting savedPosting = postingService.modifyPosting(posting);

        return PostingMapper.toResponse(savedPosting, true);
    }

    @Transactional
    public void deleteNonMemberPosting(
        PostingDeleteRequest request
    ) {
        String hashedPassword = postingService.getPostingById(request.postingId()).getPassword();
        passwordEncoder.matches(request.password().toString(), hashedPassword);

        postingService.deleteNonMemberPosting(request.postingId());
    }

    @Transactional
    public void deleteMemberPosting(
        PostingDeleteRequest request,
        MemberResponse member
    ) {
        postingService.deleteMemberPosting(request.postingId(), member.getId());
    }

    @Transactional
    public PostingResponse pullUpNonMemberPosting(
        PostingPullUpRequest request
    ) {
        String hashedPassword = postingService.getPostingById(request.postingId()).getPassword();
        passwordEncoder.matches(request.password().toString(), hashedPassword);

        Posting posting = postingService.pullUpNonMemberPosting(request.postingId());
        return PostingMapper.toResponse(posting, false);
    }

    @Transactional
    public PostingResponse pullUpMemberPosting(
        PostingPullUpRequest request,
        MemberResponse member
    ) {
        Posting posting = postingService.pullUpMemberPosting(request.postingId(), member.getId());
        return PostingMapper.toResponse(posting, true);
    }
}
