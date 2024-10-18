package com.next.genshinflow.application.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.next.genshinflow.application.user.dto.enkaApi.ProfileImgDataResponse;
import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.infrastructure.EnkaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnkaService {
    private final EnkaClient enkaClient;
    private final ObjectMapper objectMapper;

    public UserInfoResponse callExternalApi(long uid) {
        try {
            ResponseEntity<UserInfoResponse> responseEntity =
                enkaClient.getUserInfo(uid, 1L);
            UserInfoResponse response = responseEntity.getBody();

            if (response != null) {
                log.info("API Response: {}", response);
                return response;
            }
            else {
                log.warn("API Response is null");
                throw new RuntimeException("API Response is null");
            }
        }
        catch (Exception e) {
            log.error("API Error: {}", e.getMessage(), e);
            throw new RuntimeException("External API call failed", e);
        }
    }

    // 이미지 id를 경로로 반환
    public String getIconPathForProfilePicture(int profilePictureId) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/api_profile_pictures.json");
            List<ProfileImgDataResponse> profileImgDataResponses = objectMapper
                .readValue(inputStream, new TypeReference<List<ProfileImgDataResponse>>() {});

            String iconPath = profileImgDataResponses.stream()
                .filter(p -> p.getPriority() == profilePictureId)
                .map(ProfileImgDataResponse::getIconPath)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("프로필 사진을 찾을 수 없습니다."));

            return "https://enka.network/ui/" + iconPath + ".png";
        }
        catch (IOException e) {
            log.error("IOException occurred while reading profile pictures: {}", e.getMessage(), e);
            throw new RuntimeException("프로필 사진을 불러오는데 실패했습니다.", e);
        }
    }
}
