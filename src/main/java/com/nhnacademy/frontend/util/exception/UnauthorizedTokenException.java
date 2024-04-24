package com.nhnacademy.frontend.util.exception;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/24
 */
public class UnauthorizedTokenException extends RuntimeException {
    public UnauthorizedTokenException(String message) {
        super(message);
    }
}
