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
public class OAuthRequestDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String state;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private String expiresIn;
    @JsonProperty("access_token_secret")
    private String accessTokenSecret;
}
