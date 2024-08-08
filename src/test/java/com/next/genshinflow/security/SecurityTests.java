package com.next.genshinflow.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원가입 완료 후 리다이렉션")
    void signUpWithValidInput() throws Exception {
        mockMvc.perform(post("/sign-up")
            .param("email", "valid@gmail.com")
            .param("password", "testPW!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("successMessage"))
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("입력값에 오류가 있다면 회원가입 실패하고 회원가입 페이지에 오류 메시지 표시")
    void signUpWithWrongInput() throws Exception {
        mockMvc.perform(post("/sign-up")
            .param("email", "invalid-email")
            .param("password", "1234"))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors( "email", "password")) // 특정 필드에 에러가 있는지 확인
            .andExpect(view().name("sign-up"));
    }

    @Test
    @DisplayName("로그인 성공 후 리다이렉션")
    void loginWithValidCredentials() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("email", "valid@gmail.com")
                .param("password", "testPW!"));

        mockMvc.perform(post("/login")
            .param("email", "valid@gmail.com")
            .param("password", "testPW!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("로그인 실패 시 실패 메시지 노출")
    void loginWithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/login")
            .param("email", "invalidUser")
            .param("password", "invalidPassword"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"))
            .andExpect(flash().attributeExists("존재하지 않는 계정"));
    }

    @Test
    @DisplayName("인증 없이 공개된 엔드포인트에 접근하는 테스트")
    void accessUnsecuredEndpoint() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증 없이 보호된 엔드포인트에 접근")
    void accessSecuredEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/admin"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("USER 역할을 가진 사용자가 마이페이지 엔드포인트에 접근")
    @WithMockUser(username = "user", roles = {"USER"})
    void accessSecuredEndpointWithUserRole() throws Exception {
        mockMvc.perform(get("/member/my-page"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADMIN 역할을 가진 사용자가 관리자 엔드포인트에 접근")
    void accessAdminEndpointWithAdminRole() throws Exception {
        mockMvc.perform(get("/admin"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("USER 역할을 가진 사용자가 관리자 엔드포인트에 접근")
    void accessAdminEndpointWithUserRole() throws Exception {
        mockMvc.perform(get("/admin"))
            .andExpect(status().isForbidden());
    }
}
