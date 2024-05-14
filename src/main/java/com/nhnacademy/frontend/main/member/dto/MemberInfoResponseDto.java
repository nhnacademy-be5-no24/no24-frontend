package com.nhnacademy.frontend.main.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Member 기본 정보를 받기 위한 입니다.
 *
 * @author : 강병구
 * @date : 2024-05-02
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemberInfoResponseDto {
    private String memberId;
    private String memberName;
    private String email;
    private String phoneNumber;
    private LocalDateTime lastLoginAt;
    private String role;
}
