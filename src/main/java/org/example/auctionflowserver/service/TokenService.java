package org.example.auctionflowserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;

    public String refreshAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        // 토큰 갱신 요청
        Map<String, String> request = new HashMap<>();
        request.put("grant_type", "refresh_token");
        request.put("client_id", clientId);
        request.put("client_secret", clientSecret);
        request.put("refresh_token", refreshToken);

        // 응답에서 새로운 액세스 토큰 추출
        Map<String, Object> response = restTemplate.postForObject(tokenUri, request, Map.class);
        String newAccessToken = (String) response.get("access_token");

        return newAccessToken;
    }
}