package com.next.genshinflow.intergration.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("회원가입 완료 후 리다이렉션")
    @Test
    void signUpWithValidInput() throws Exception {
        String email = "valid@gmail.com";
        String password = "testPW!";

        ResultActions actions = mockMvc.perform(post("/sign-up")
                .param("email", email)
                .param("password", password));

        actions.andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("successMessage"))
            .andExpect(redirectedUrl("/login"));
    }

    @DisplayName("입력값에 오류가 있다면 회원가입 실패하고 회원가입 페이지에 오류 메시지 표시")
    @Test
    void signUpWithWrongInput() throws Exception {
        String invalidEmail = "invalid-email";
        String shortPassword = "1234";

        ResultActions actions = mockMvc.perform(post("/sign-up")
                .param("email", invalidEmail)
                .param("password", shortPassword));

        actions.andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("email", "password"))
            .andExpect(view().name("sign-up"));
    }

    @DisplayName("로그인 성공 후 리다이렉션")
    @Test
    void loginWithValidCredentials() throws Exception {
        String email = "valid@gmail.com";
        String password = "testPW!";

        mockMvc.perform(post("/sign-up")
            .param("email", email)
            .param("password", password));

        ResultActions actions = mockMvc.perform(post("/login")
                .param("email", email)
                .param("password", password));

        actions.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @DisplayName("로그인 실패 시 실패 메시지 노출")
    @Test
    void loginWithInvalidCredentials() throws Exception {
        String invalidEmail = "invalidUser";
        String invalidPassword = "invalidPassword";

        ResultActions actions = mockMvc.perform(post("/login")
                .param("email", invalidEmail)
                .param("password", invalidPassword));

        actions.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    @DisplayName("인증 없이 공개된 엔드포인트에 접근하는 테스트")
    @Test
    void accessUnsecuredEndpoint() throws Exception {
        ResultActions actions = mockMvc.perform(get("/"));

        actions.andExpect(status().isOk());
    }

    @DisplayName("인증 없이 보호된 엔드포인트에 접근")
    @Test
    void accessSecuredEndpointWithoutAuthentication() throws Exception {
        ResultActions actions = mockMvc.perform(get("/admin"));

        actions.andExpect(status().isUnauthorized());
    }

    @DisplayName("USER 역할을 가진 사용자가 마이페이지 엔드포인트에 접근")
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void accessSecuredEndpointWithUserRole() throws Exception {
        ResultActions actions = mockMvc.perform(get("/member/my-page"));

        actions.andExpect(status().isOk());
    }

    @DisplayName("ADMIN 역할을 가진 사용자가 관리자 엔드포인트에 접근")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void accessAdminEndpointWithAdminRole() throws Exception {
        ResultActions actions = mockMvc.perform(get("/admin"));

        actions.andExpect(status().isOk());
    }

    @DisplayName("USER 역할을 가진 사용자가 관리자 엔드포인트에 접근")
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void accessAdminEndpointWithUserRole() throws Exception {
        ResultActions actions = mockMvc.perform(get("/admin"));

        actions.andExpect(status().isForbidden());
    }
}
