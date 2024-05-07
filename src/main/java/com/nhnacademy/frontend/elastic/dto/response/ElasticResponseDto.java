package com.nhnacademy.frontend.elastic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 도서 검색 응답 결과 Dto 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */

// TODO #1 Book Publisher 추가 후 Dto 수정 필요
@Getter
@AllArgsConstructor
public class ElasticResponseDto {
    private String bookIsbn;
    private String bookTitle;
    private String bookDescription;
    private String bookPublisher;
    private LocalDateTime bookPublishedAt;
    private Long bookFixedPrice;
    private Long bookSalePrice;
    private Long bookViews;
    private String author;
    private String bookImage;
    private Integer bookStatus;
    private Long likes;
}
