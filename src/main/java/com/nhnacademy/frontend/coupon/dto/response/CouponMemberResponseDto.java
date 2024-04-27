package com.nhnacademy.frontend.coupon.dto.response;

import com.nhnacademy.frontend.coupon.dto.CouponTarget;
import com.nhnacademy.frontend.coupon.dto.CouponType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * CouponMember의 응답 Dto
 *
 * @Author : 박병휘
 * @Date : 2024/04/21
 */
@Builder
@Getter
public class CouponMemberResponseDto {
    public enum Status {
        ACTIVE, USED, DESTROYED
    }

    private Long couponMemberId;
    @NonNull
    private Long couponId;
    @NonNull
    private Long customerNo;
    private String couponName;
    @NonNull
    private LocalDateTime createdAt;
    private LocalDateTime destroyedAt;
    private LocalDateTime usedAt;
    private boolean used;

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
