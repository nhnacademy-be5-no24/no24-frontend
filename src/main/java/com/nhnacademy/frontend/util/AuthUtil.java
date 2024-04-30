package com.nhnacademy.frontend.util;

import com.nhnacademy.frontend.util.exception.NotFoundToken;
import com.nhnacademy.frontend.util.exception.UnauthorizedTokenException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/24
 */
public class AuthUtil {
    public static Long getCustomerNo(String requestUrl,
                              String port,
                              HttpServletRequest request,
                              RedisTemplate redisTemplate,
                              RestTemplate restTemplate) {
        HttpSession session = request.getSession();
        String jSessionId = session.getId();

        // get user information
        HashOperations<String, String, String> authHashOperations = redisTemplate.opsForHash();
        String authorization = authHashOperations.get(jSessionId, "Authorization");
        String refreshToken = authHashOperations.get(jSessionId, "RefreshToken");

        if(authorization == null || authorization.equals("")) {
            throw new NotFoundToken("Token is not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        headers.add("RefreshToken", refreshToken);

        // get customerNo
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(
                requestUrl + ":" + port +  "/auth/get/customerNo",
                new HttpEntity<>(null, headers),
                Long.class
        );

        if(responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new UnauthorizedTokenException("Unauthorized token");
        }

        Long customerNo = responseEntity.getBody();

        return customerNo;
    }
}
