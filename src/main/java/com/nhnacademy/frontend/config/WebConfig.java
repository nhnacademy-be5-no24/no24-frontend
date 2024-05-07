package com.nhnacademy.frontend.config;

import com.nhnacademy.frontend.interceptor.HeaderAddInterceptor;
import com.nhnacademy.frontend.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/23
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    @Bean
    public HeaderAddInterceptor headerAddInterceptor() {
        return new HeaderAddInterceptor(restTemplate, redisTemplate);
    }

    @Bean
    public LoginCheckInterceptor loginCheckInterceptor() {
        return new LoginCheckInterceptor(restTemplate, redisTemplate);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerAddInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/book/**")
            .excludePathPatterns("/logout");

        registry.addInterceptor(loginCheckInterceptor())
                .addPathPatterns("/coupon**")
                .addPathPatterns("/info**")
                .addPathPatterns("/info/**")
                .addPathPatterns("/admin**");
    }
}
