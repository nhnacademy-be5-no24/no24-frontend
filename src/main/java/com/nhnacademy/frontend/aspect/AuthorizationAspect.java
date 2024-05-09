package com.nhnacademy.frontend.aspect;

import com.nhnacademy.frontend.main.member.dto.MemberInfoResponseDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * Header 정보를 받아 인가하기 위한 Aspect 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-26
 */

@Component
@Aspect
public class AuthorizationAspect {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    public AuthorizationAspect(RestTemplate restTemplate, RedisTemplate redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }


    @Around("execution(* com.nhnacademy.frontend.admin..*(..))")
    public Object aroundMethod(ProceedingJoinPoint pjp) throws Throwable {
        // get session
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpSession session = request.getSession();
        String jSessionId = session.getId();

        // get user information
        HashOperations<String, String, String> authHashOperations = redisTemplate.opsForHash();
        String token = authHashOperations.get(jSessionId, "Authorization").substring(7);

        if(token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseEntity<MemberInfoResponseDto> memberDto = restTemplate
                .getForEntity(requestUrl + ":" + port + "/auth/member/token/" + token, MemberInfoResponseDto.class);

        MemberInfoResponseDto memberInfoResponseDto = memberDto.getBody();
        if (!Objects.nonNull(memberInfoResponseDto)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!memberInfoResponseDto.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return pjp.proceed();
    }
}
