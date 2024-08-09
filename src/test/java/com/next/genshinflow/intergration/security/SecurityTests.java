package com.next.genshinflow.intergration.security;

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
        // 회원가입 폼에 올바른 데이터를 입력하고
        String email = "valid@gmail.com";
        String password = "testPW!";

        // 회원가입 요청을 전송하면
        mockMvc.perform(post("/sign-up")
                .param("email", email)
                .param("password", password))

            // 회원가입이 성공하고, 로그인 페이지로 리다이렉션된다.
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("successMessage"))
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("입력값에 오류가 있다면 회원가입 실패하고 회원가입 페이지에 오류 메시지 표시")
    void signUpWithWrongInput() throws Exception {
        // 잘못된 이메일과 비밀번호를 입력한 상태에서
        String invalidEmail = "invalid-email";
        String shortPassword = "1234";

        // 회원가입 요청을 전송하면
        mockMvc.perform(post("/sign-up")
                .param("email", invalidEmail)
                .param("password", shortPassword))

            // 회원가입이 실패하고, 오류 메시지가 표시된 상태로 회원가입 페이지를 반환한다.
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("email", "password"))
            .andExpect(view().name("sign-up"));
    }

    @Test
    @DisplayName("로그인 성공 후 리다이렉션")
    void loginWithValidCredentials() throws Exception {
        // 유효한 계정 정보로 회원가입을 완료한 후
        String email = "valid@gmail.com";
        String password = "testPW!";

        mockMvc.perform(post("/sign-up")
            .param("email", email)
            .param("password", password));

        // 해당 정보로 로그인을 시도하면
        mockMvc.perform(post("/login")
                .param("email", email)
                .param("password", password))

            // 로그인이 성공하고, 홈 페이지로 리다이렉션된다.
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("로그인 실패 시 실패 메시지 노출")
    void loginWithInvalidCredentials() throws Exception {
        // 존재하지 않는 계정 정보로
        String invalidEmail = "invalidUser";
        String invalidPassword = "invalidPassword";

        // 로그인을 시도하면
        mockMvc.perform(post("/login")
                .param("email", invalidEmail)
                .param("password", invalidPassword))

            // 로그인이 실패하고, 로그인 페이지로 리다이렉션되며 오류 메시지가 표시된다.
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"))
            .andExpect(flash().attributeExists("errorMessage"));
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
    // USER 역할을 가진 사용자가 있을 때
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("USER 역할을 가진 사용자가 마이페이지 엔드포인트에 접근")
    void accessSecuredEndpointWithUserRole() throws Exception {
        // 해당 사용자가 마이페이지 엔드포인트 ("/member/my-page")에 접근하면
        mockMvc.perform(get("/member/my-page"))
            // 접근이 허용되고, HTTP 200 OK 상태를 반환한다.
            .andExpect(status().isOk());
    }

    @Test
    // ADMIN 역할을 가진 사용자가 있을 때
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADMIN 역할을 가진 사용자가 관리자 엔드포인트에 접근")
    void accessAdminEndpointWithAdminRole() throws Exception {
        // 해당 사용자가 관리자 엔드포인트 ("/admin")에 접근하면
        mockMvc.perform(get("/admin"))
            // 접근이 허용되고, HTTP 200 OK 상태를 반환한다.
            .andExpect(status().isOk());
    }

    @Test
    // USER 역할을 가진 사용자가 있을 때
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("USER 역할을 가진 사용자가 관리자 엔드포인트에 접근")
    void accessAdminEndpointWithUserRole() throws Exception {
        // 해당 사용자가 관리자 엔드포인트 ("/admin")에 접근하면
        mockMvc.perform(get("/admin"))
            // 접근이 거부되고, HTTP 403 Forbidden 상태를 반환한다.
            .andExpect(status().isForbidden());
    }
}
