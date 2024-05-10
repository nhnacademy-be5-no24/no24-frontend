package com.nhnacademy.frontend.interceptor;

import com.nhnacademy.frontend.auth.dto.LoginDto;
import com.nhnacademy.frontend.auth.dto.TokenDto;
import com.nhnacademy.frontend.main.member.dto.MemberInfoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/22
 */
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;

    public LoginCheckInterceptor(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
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
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }

    public boolean isLoggedIn(String jSessionId) {
        String token = (String) redisTemplate.opsForHash().get(jSessionId, "Authorization");
        String refreshToken = (String) redisTemplate.opsForHash().get(jSessionId, "RefreshToken");

        return token != null && refreshToken != null;
    }

    public boolean isAdmin(String jSessionId) {
        String token = (String) redisTemplate.opsForHash().get(jSessionId, "Authorization");
        token = token.substring(7);

        ResponseEntity<MemberInfoResponseDto> memberDto = restTemplate
                .getForEntity(requestUrl + ":" + port + "/auth/member/token/" + token, MemberInfoResponseDto.class);

        if(memberDto.getBody().getRole().equals("ROLE_ADMIN"))
            return true;

        return false;
    }
}
