package com.nhnacademy.frontend.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 포인트 내역 정보를 반환하기 위한 Dto 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-04
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointResponseDto {
    private Long pointId;
    private Long customerNo;
    private String orderId;
    private String pointDescription;
    private Integer usage;
    private LocalDateTime createdAt;
}
