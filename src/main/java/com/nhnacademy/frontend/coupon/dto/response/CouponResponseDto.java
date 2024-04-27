package com.nhnacademy.frontend.coupon.dto.response;

import com.nhnacademy.frontend.coupon.dto.CouponTarget;
import com.nhnacademy.frontend.coupon.dto.CouponType;
import com.nhnacademy.frontend.coupon.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 쿠폰 기본 정보를 반환하기 위한 dto 입니다.
 *
 * @author : 박병휘
 * @date : 2024/03/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDto {

    private Long couponId;
    private String couponName;
    private Date deadline;
    private int issueLimit;
    private Status couponStatus;
    private CouponType couponType;
    private CouponTarget couponTarget;

    // for target
    private String bookIsbn;
    private Long categoryId;

    // for coupon type
    private Long discountPrice;
    private Long discountRate;
    private Long maxDiscountPrice;
}
