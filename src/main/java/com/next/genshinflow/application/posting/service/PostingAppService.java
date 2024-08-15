package com.next.genshinflow.application.posting.service;

import com.next.genshinflow.application.PageResponse;
import com.next.genshinflow.application.posting.mapper.PostingMapper;
import com.next.genshinflow.application.posting.request.PostingCreateRequest;
import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.application.user.response.MemberResponse;
import com.next.genshinflow.domain.posting.Posting;
import com.next.genshinflow.domain.posting.PostingService;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.util.HashedPassword;
import com.next.genshinflow.util.PasswordUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostingAppService {

    private final PostingService postingService;
    private final MemberRepository memberRepository;

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
        return member != null && target.getUid() == member.uid();
    }

    @Transactional
    public PostingResponse createNonMemberPosting(PostingCreateRequest request) {
        HashedPassword hashedPassword = PasswordUtils.hashWithSalt(request.password());

        Posting posting = PostingMapper.from(request, null, hashedPassword);
        Posting savedPosting = postingService.createPosting(posting);

        return PostingMapper.toResponse(savedPosting, false);
    }

    @Transactional
    public PostingResponse createMemberPosting(
        PostingCreateRequest request,
        MemberResponse member
    ) {
        MemberEntity writer = memberRepository.findById(member.id())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Posting posting = PostingMapper.from(request, writer, HashedPassword.EMPTY);
        Posting savedPosting = postingService.createPosting(posting);

        return PostingMapper.toResponse(savedPosting, true);
    }

}
