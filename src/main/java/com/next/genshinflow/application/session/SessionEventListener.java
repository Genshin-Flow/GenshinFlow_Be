package com.next.genshinflow.application.session;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionEventListener {
    private final SessionManager sessionManager;

    @EventListener
    public void handleLoginEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String userId = authentication.getName();
        sessionManager.userConnected(userId);
    }

    @EventListener
    public void handleLogoutEvent(LogoutSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String userId = authentication.getName();
        sessionManager.userDisconnected(userId);
    }
}
