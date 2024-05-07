package com.nhnacademy.frontend.interceptor;

import com.nhnacademy.frontend.auth.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/22
 */
public class HeaderAddInterceptor implements HandlerInterceptor {
    @Value("${request.url}")
    private String requestUrl;
    @Value("${request.port}")
    private String port;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;

    public HeaderAddInterceptor(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String jSessionId = session.getId();
        String token = (String) redisTemplate.opsForHash().get(jSessionId, "Authorization");
        String refreshToken = (String) redisTemplate.opsForHash().get(jSessionId, "RefreshToken");

        if(token == null || token.equals("")) {
            return true;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.set("Authorization", token);
        httpHeaders.set("RefreshToken", refreshToken);

        // token 확인
        ResponseEntity<TokenDto> responseEntity = restTemplate.exchange(
                requestUrl + ":" + port +  "/auth/verify",
                HttpMethod.POST,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<>() {}
        );

        // token이 인증된 경우
        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            return true;
        }
        else if(responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // token이 unauthorized인 경우
            response.sendRedirect("/login");
            return false;
        }
        else {
            // token이 created인 경우
            String newAuthorization = responseEntity.getBody().getAuthorization();
            String newRefreshToken = responseEntity.getBody().getRefreshToken();

            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

            hashOperations.delete(jSessionId, "Authorization");
            hashOperations.delete(jSessionId, "RefreshToken");
            hashOperations.put(jSessionId, "Authorization", newAuthorization);
            hashOperations.put(jSessionId, "RefreshToken", newRefreshToken);
        }

        return true;
    }
}
