package com.nhnacademy.frontend.util;

import lombok.Getter;

/**
 * email 정보를 받는 dto
 *
 * @Author : 박병휘
 * @Date : 2024/05/14
 */
@Getter
public class AuthorizationDto {
    private String authorizationCode;
    private String memberId;
}