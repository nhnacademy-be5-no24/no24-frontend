package com.nhnacademy.frontend.config;

import com.nhnacademy.frontend.interceptor.HeaderAddInterceptor;
import com.nhnacademy.frontend.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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
    private final LoginCheckInterceptor loginCheckInterceptor;
    private final HeaderAddInterceptor headerAddInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerAddInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/book/**")
            .excludePathPatterns("/logout");

        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/cart**")
                .addPathPatterns("/order**")
                .addPathPatterns("/coupon**")
                .addPathPatterns("/info**")
                .addPathPatterns("/info/**")
                .addPathPatterns("/admin**");
    }
}
