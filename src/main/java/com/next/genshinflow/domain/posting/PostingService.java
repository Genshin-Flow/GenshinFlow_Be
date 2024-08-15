package com.next.genshinflow.domain.posting;

import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostingService {

    private final PostingRepository postingRepository;

    public Page<Posting> getDashboardPostings(Pageable pageable) {
        return postingRepository.findAllByDeletedFalseAndCompletedAtAfterOrderByUpdatedAtDesc(
            LocalDateTime.now(),
            pageable
        );
    }

    public Posting getPostingById(long postingId) {
        return postingRepository.findById(postingId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));
    }

    @Transactional
    public Posting createPosting(Posting posting) {
        return postingRepository.save(posting);
    }

    @Transactional
    public Posting modifyPosting(Posting posting) {
        Posting source = postingRepository.findById(posting.getId())
            .filter(it -> this.isWriter(posting.getWriter().getId(), it))
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));

        posting.setId(source.getId());
        posting.setDeleted(false);
        posting.setCreatedAt(source.getCreatedAt());

        return postingRepository.save(posting);
    }

    @Transactional
    public Posting pullUpPosting(long postingId) {
        return postingRepository.findById(postingId)
            .map(this::setUpdatedAtToNow)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));
    }

    @Transactional
    public void deleteNonMemberPosting(long postingId) {
        postingRepository.findById(postingId)
            .map(posting -> posting.setDeleted(true))
            .ifPresentOrElse(
                postingRepository::save,
                () -> {
                    throw new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND);
                }
            );
    }

    @Transactional
    public void deleteMemberPosting(long postingId, long accountId) {
        postingRepository.findById(postingId)
            .filter(posting -> this.isWriter(accountId, posting))
            .map(posting -> posting.setDeleted(true))
            .ifPresentOrElse(
                postingRepository::save,
                () -> {
                    throw new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND);
                }
            );
    }

    private boolean isWriter(long accountId, Posting posting) {
        return Optional.ofNullable(posting)
            .map(Posting::getWriter)
            .map(MemberEntity::getId)
            .filter(id -> id == accountId)
            .isPresent();
    }

    private Posting setUpdatedAtToNow(Posting posting) {
        posting.setUpdatedAt(LocalDateTime.now());
        return posting;
    }
}
