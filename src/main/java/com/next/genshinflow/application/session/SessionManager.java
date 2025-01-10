package com.next.genshinflow.application.session;

import com.next.genshinflow.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SessionManager {
    private final Set<String> activeUsers = ConcurrentHashMap.newKeySet();
    private final PostRepository postRepository;

    // 사용자가 로그인하면 등록
    public void userConnected(String userId) {
        activeUsers.add(userId);
    }

    // 사용자가 로그아웃하거나 세션이 종료되면 제거
    public void userDisconnected(String userId) {
        activeUsers.remove(userId);
    }

    // 현재 접속 중인 사용자 수
    public int getActiveUserCount() {
        return activeUsers.size();
    }

    // 오늘 작성된 게시글 수
    public int getTodayPost() {
        return postRepository.countTodayPosts();
    }
}
