package com.nhnacademy.frontend.main.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateReviewRequestDto {
    private String reviewContent;
    private Integer reviewScore;
    private String reviewImage;
    private String bookIsbn;
    private Long customerNo;
}
