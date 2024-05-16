package com.nhnacademy.frontend.main.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPageResponseDto {
    private List<ReviewResponseDto> reviewList;
//    private int maxPage;
}
