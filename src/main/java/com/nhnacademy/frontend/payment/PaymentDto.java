package com.nhnacademy.frontend.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/25
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private String paymentName;
}
