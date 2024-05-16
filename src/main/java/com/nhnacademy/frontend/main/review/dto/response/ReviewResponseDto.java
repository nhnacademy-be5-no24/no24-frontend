package com.nhnacademy.frontend.main.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 리뷰 기본정보를 반환하기 위한 dto 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-01
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private String reviewContent;
    private Integer reviewScore;
    private String reviewImage;
    private String bookIsbn;
    private Long customerNo;
    private LocalDate createdAt;
}
