package com.nhnacademy.frontend.grade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 등급 요청 dto
 *
 * @Author : 박병휘
 * @Date : 2024/05/03
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GradeRequestDto {
    private Long gradeId;
    private String gradeName;
    private String gradeNameKor;
    private Long accumulateRate;
    private Integer tenPercentCoupon;
    private Integer twentyPercentCoupon;
    private Long requiredPayment;

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }
}
