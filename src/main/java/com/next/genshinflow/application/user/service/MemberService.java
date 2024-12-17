package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.mapper.PostMapper;
import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.application.user.dto.member.ChangeUidRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.post.repository.PostRepository;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;


    @Transactional(readOnly = true)
    public MemberResponse getMyInfo() {
        return MemberMapper.memberToResponse(
            SecurityUtil.getCurrentUsername()
                .flatMap(memberRepository::findByEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND))
        );
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberInfo() {
        MemberEntity member = authService.getCurrentMember();
        return MemberMapper.memberToResponse(member);
    }

    public MemberResponse updateUid(ChangeUidRequest request) {
        MemberEntity findMember = authService.findMember(request.getUserEmail());

        findMember.setUid(request.getUid());
        MemberEntity updatedMember = memberRepository.save(findMember);

        UserInfoResponse apiResponse = authService.getUserInfoFromApi(request.getUid());
        authService.updateMemberIfPlayerInfoChanged(updatedMember, apiResponse);

        return MemberMapper.memberToResponse(updatedMember);
    }

    public Page<PostResponse> getMyPosts(int page, int size) {
        MemberEntity member = authService.getCurrentMember();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<PostEntity> historyPage = postRepository.findByWriter_Id(member.getId(), pageable);

        return historyPage.map(PostMapper::toResponse);
    }
}
