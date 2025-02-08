package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.BasePageResponse;
import com.next.genshinflow.application.EntityFinder;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.mapper.PostMapper;
import com.next.genshinflow.application.user.dto.member.ChangePasswordRequest;
import com.next.genshinflow.application.user.dto.member.ChangeUidRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.application.validation.UserValidationManager;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.post.repository.PostRepository;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final EntityFinder entityFinder;
    private final UserValidationManager validationManager;
    private final PasswordEncoder passwordEncoder;
    private final RedisRepository redisRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final UserProfileService userProfileService;

    // 내 정보 조회
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo() {
        return MemberMapper.memberToResponse(
            SecurityUtil.getCurrentUsername()
                .flatMap(memberRepository::findByEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND))
        );
    }

    // 유저 정보 조회 (관리자 권한)
    @Transactional(readOnly = true)
    public MemberResponse getMemberInfo(String email) {
        MemberEntity member = entityFinder.findMemberByEmail(email);
        return MemberMapper.memberToResponse(member);
    }

    // 내 게시물 조회
    public BasePageResponse<PostResponse> getMyPosts(int page, int size) {
        MemberEntity member = entityFinder.getCurrentMember();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<PostEntity> historyPage = postRepository.findByWriter_Id(member.getId(), pageable);

        Page<PostResponse> postResponsePage = historyPage.map(PostMapper::toResponse);
        return new BasePageResponse<>(
            postResponsePage.getContent(),
            postResponsePage.getNumber() + 1,
            postResponsePage.getSize(),
            postResponsePage.getTotalElements(),
            postResponsePage.getTotalPages()
        );
    }

    public MemberResponse updateUid(ChangeUidRequest request) {
        MemberEntity findMember = entityFinder.findMemberByEmail(request.getEmail());

        findMember.setUid(request.getUid());
        MemberEntity updatedMember = memberRepository.save(findMember);

        userProfileService.updateMemberIfPlayerInfoChanged(updatedMember);

        return MemberMapper.memberToResponse(updatedMember);
    }

    // 일반 유저 비밀번호 변경
    public void changePassword(ChangePasswordRequest request) {
        validationManager.verifyAuthCode(request.getEmail(), request.getAuthNum());
        MemberEntity member = entityFinder.findMemberByEmail(request.getEmail());
        validationManager.validatePasswordNotSame(request.getPassword(), member.getPassword());

        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setFailedLoginAttempts(0);

        redisRepository.deleteData(request.getAuthNum());
        memberRepository.save(member);
    }
}
