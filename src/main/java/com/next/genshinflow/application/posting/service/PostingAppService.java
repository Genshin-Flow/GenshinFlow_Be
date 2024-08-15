package com.next.genshinflow.application.posting.service;

import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.domain.posting.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostingAppService {

    private final PostingService postingService;

    public Page<PostingResponse> getDashboardPostings(Pageable pageable) {
        return postingService.getDashboardPostings(pageable);
    }

}
