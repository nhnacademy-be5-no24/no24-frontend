package com.nhnacademy.frontend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * OAuthDto
 *
 * @Author : 박병휘
 * @Date : 2024/05/07
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OAuthResponseDto {
    private String idNo;
    private String email;
    private String mobile;
    private String maskedEmail;
    private String maskedMobile;
    private String name;
    private String genderCode;
    private String birthdayMMdd;
    private String ageGroup;
}
