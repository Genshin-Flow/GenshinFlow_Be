package com.next.genshinflow.application.user.service;

import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.enumeration.AccountStatus;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberEntity createMember(MemberEntity member) {
        verifyExistEmail(member.getEmail());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        member.setRole(Role.USER);

        MemberEntity savedMember = memberRepository.save(member);

        if (member.getName().length() <= 0) {
            member.setName("모시깽이한 유저" + savedMember.getId());
            savedMember = memberRepository.save(member);
        }

        return savedMember;
    }

    public MemberEntity createMemberOAuth2(MemberEntity member) {
        return null;
    }

    public MemberEntity setProfileImg(long memberId) {
        return null;
    }

    public MemberEntity updateMember(long loginId, MemberEntity member) {
        verifyPermission(loginId, member.getId());
        MemberEntity findMember = findMember(member.getId());

        if (member.getUid().equals(findMember.getUid())) {
            findMember.setUid(member.getUid());
        }

        // 이름 중복이 가능한지 회의 필요함
        if (member.getName().equals(findMember.getName())) {
            findMember.setName(member.getName());
        }

        return findMember;
    }

    public MemberEntity updatePassword(long loginId, MemberEntity member) {
        return null;
    }

    public MemberEntity updateActiveStatus(long memberId) {
        MemberEntity findMember = findMember(memberId);
        findMember.setStatus(AccountStatus.ACTIVE_USER);

        return findMember;
    }

    public MemberEntity updateDeleteStatus(long memberId) {
        MemberEntity findMember = findMember(memberId);
        findMember.setStatus(AccountStatus.DELETED_USER);

        return findMember;
    }

    // 개발자, 관리자 용
    public void deleteMember(long loginId, long memberId) {
        verifyPermission(loginId, memberId);
        MemberEntity member = findMember(memberId);

        memberRepository.delete(member);
    }

    // 이메일 검증
    private void verifyExistEmail(String email) {
        Optional<MemberEntity> member = memberRepository.findByEmail(email);
        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    // 해당 사용자, 권한 검증
    public void verifyPermission(long loginId, long memberId) {
        MemberEntity member = findMember(loginId);

        if (member.getRole() != Role.ADMIN) {
            if (loginId != memberId) {
                throw new BusinessLogicException(ExceptionCode.NO_PERMISSION);
            }
        }
    }

    public MemberEntity findMember(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
