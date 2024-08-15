package com.next.genshinflow.domain.posting;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
            pageable);
    }

    public Posting findPostingById(long id) {
        return postingRepository.findById(id)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));
    }

    @Transactional
    public Posting createNonMembersPosting() {

    }

    @Transactional
    public Posting modifyPosting(long postingId,) {

    }

    @Transactional
    public Posting pullUpPosting(long postingId) {
        return postingRepository.findById(postingId)
            .map(this::setUpdatedAtToNow)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));
    }

    public void deletePosting(long id) {
        postingRepository.deleteById(id);
    }

    private Posting setUpdatedAtToNow(Posting posting) {
        posting.setUpdatedAt(LocalDateTime.now());
        return posting;
    }
}
