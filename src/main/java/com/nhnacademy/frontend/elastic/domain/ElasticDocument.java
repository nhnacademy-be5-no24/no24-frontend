package com.nhnacademy.frontend.elastic.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 검색에 사용되는 도서 Document 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */

@Getter
@NoArgsConstructor
@Document(indexName = "no24-book")
public class ElasticDocument {
    @Id
    private String bookIsbn;
    @Field(name = "book_title")
    private String bookTitle;
    @Field(name = "book_desc")
    private String bookDesc;
//    @Field(name = "book_publisher")
//    private String bookPublisher;
    @Field(name = "book_published_at")
    private LocalDateTime bookPublishedAt;
    @Field(name = "book_fixed_price")
    private Long bookFixedPrice;
    @Field(name = "book_sale_price")
    private Long bookSalePrice;
    @Field(name = "book_views")
    private Long bookViews;
    @Field(name = "author")
    private String author;
    @Field(name = "book_image")
    private String bookImage;
    @Field(name =  "book_status")
    private Integer bookStatus;
    @Field(name = "likes")
    private Long likes;
}
