package com.next.genshinflow.application.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.next.genshinflow.application.user.dto.ProfileImgDataResponse;
import com.next.genshinflow.application.user.dto.UserInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Autowired
    public ApiService(WebClient.Builder webClientBuilder,
                      ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    public Mono<UserInfoResponse> callExternalApi(long uid) {
        return webClientBuilder.build()
            .get()
            .uri("https://enka.network/api/uid/" + uid + "?info")
            .retrieve()
            .bodyToMono(UserInfoResponse.class)
            .doOnNext(response -> logger.info("API Response: {}", response))
            .doOnError(error -> logger.error("API Error: {}", error.getMessage()));
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
            e.printStackTrace();
            throw new RuntimeException("프로필 사진을 불러오는데 실패했습니다.", e);
        }
    }
}
