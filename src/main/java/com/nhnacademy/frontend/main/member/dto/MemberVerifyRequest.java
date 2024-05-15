package com.nhnacademy.frontend.main.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberVerifyRequest {
    private Long customerNo;
    private String customerPassword;
}
