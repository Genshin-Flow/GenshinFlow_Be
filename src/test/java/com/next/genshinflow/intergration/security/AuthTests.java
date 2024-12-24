package com.next.genshinflow.intergration.security;

import com.next.genshinflow.application.user.dto.auth.LoginRequest;
import com.next.genshinflow.application.user.dto.auth.SignUpRequest;
import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.application.user.service.EnkaService;
import com.next.genshinflow.application.user.service.MailSendService;
import com.nimbusds.jose.shaded.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnkaService enkaService;

    @MockBean
    private MailSendService mailSendService;

    private static final long UID = 12345678L;
    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "testPW1234!";
    private static final String AUTH_NUM = "123456";

    @DisplayName("회원가입 요청 테스트")
    @Test
    void signUpTest() throws Exception {
        // 가상의 API 응답을 미리 설정
        UserInfoResponse mockApiResponse = new UserInfoResponse();
        // 필요한 필드들 설정
        mockApiResponse.setPlayerInfo(new UserInfoResponse
            .PlayerInfo("닉네임", 50, 8, 10, 2, new UserInfoResponse.ProfilePicture(100)));

        when(enkaService.getUserInfoFromApi(UID)).thenReturn(mockApiResponse);
        when(enkaService.getIconPathForProfilePicture(100)).thenReturn("https://enka.network/ui/someIcon.png");

        // MailSendService의 인증번호 확인 로직을 Mock 처리
        doNothing().when(mailSendService).verifyAuthCode(EMAIL, AUTH_NUM);

        SignUpRequest signUpRequest = SignUpRequest.builder()
            .uid(UID)
            .email(EMAIL)
            .password(PASSWORD)
            .authNum(AUTH_NUM)
            .build();

        String json = new Gson().toJson(signUpRequest);

        ResultActions resultActions = mockMvc.perform(post("/auth/sign-up")
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

        ResultActions resultActions = mockMvc.perform((post("/auth/sign-in")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)));

        resultActions.andExpect(status().isOk());
    }
}