package com.next.genshinflow.application;
import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.domain.post.repository.PostRepository;
import com.next.genshinflow.domain.report.entity.ReportEntity;
import com.next.genshinflow.domain.report.repository.ReportRepository;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntityFinder {
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    public MemberEntity findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public MemberEntity findMemberById(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public MemberEntity getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user)
            return findMemberByEmail(user.getUsername());

        throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
    }

    public ReportEntity findReport(long reportId) {
        return reportRepository.findById(reportId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.REPORT_NOT_FOUND));
    }

    public PostEntity findPost(long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
    }
}
