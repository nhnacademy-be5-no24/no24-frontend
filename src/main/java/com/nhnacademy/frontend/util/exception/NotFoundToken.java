package com.nhnacademy.frontend.util.exception;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/24
 */
public class NotFoundToken extends RuntimeException {
    public NotFoundToken(String message) {
        super(message);
    }
}
