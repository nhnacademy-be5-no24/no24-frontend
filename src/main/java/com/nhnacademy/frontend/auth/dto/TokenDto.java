package com.nhnacademy.frontend.auth.dto;

import lombok.*;

/**
 * Token 재발급 시 필요한 dto
 *
 * @Author : 박병휘
 * @Date : 2024/04/23
 */
@Data
public class TokenDto {
    private String authorization;
    private String refreshToken;
}
