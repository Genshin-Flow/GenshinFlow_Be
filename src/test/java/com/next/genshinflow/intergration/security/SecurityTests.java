package com.next.genshinflow.intergration.security;

import com.next.genshinflow.application.user.dto.LoginRequest;
import com.next.genshinflow.application.user.dto.SignUpRequest;
import com.nimbusds.jose.shaded.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityTests {
    @Autowired
    private MockMvc mockMvc;

    private static final long UID = 12345678L;
    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "testPW1234!";

    @DisplayName("회원가입 요청 테스트")
    @Test
    void signUpTest() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .uid(UID)
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        String json = new Gson().toJson(signUpRequest);

        ResultActions resultActions = mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        resultActions.andExpect(status().isCreated());
    }

    @DisplayName("로그인 요청 테스트")
    @Test
    void signInTest() throws Exception {
        signUpTest();

        LoginRequest loginRequest = LoginRequest.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();

        String json = new Gson().toJson(loginRequest);

        ResultActions resultActions = mockMvc.perform((post("/auth/signin")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)));

        resultActions.andExpect(status().isOk());
    }
}
