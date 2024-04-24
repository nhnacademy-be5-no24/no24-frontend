package com.nhnacademy.frontend.common.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

/**
 * @Author: jinjiwon
 * @Date: 16/04/2024
 */
@Component
public class CookieUtils {
    public Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        return cookie;
    }

    public Cookie createCookieWithoutMaxAge(String name,String value){
        Cookie cookie = new Cookie(name, value);

        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}
