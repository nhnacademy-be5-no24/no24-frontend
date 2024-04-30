package com.nhnacademy.frontend.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 쿠폰 정보 리스트를 담는 dto
 *
 * @Author : 박병휘
 * @Date : 2024/04/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDtoList {
    List<CouponResponseDto> couponResponseDtoList;
}
