package com.nhnacademy.frontend.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CouponMember의 응답 Dto List
 *
 * @Author : 박병휘
 * @Date : 2024/04/26
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponMemberResponseDtoList {
    private List<CouponMemberResponseDto> couponMemberResponseDtoList;
    private int maxPage;
}
