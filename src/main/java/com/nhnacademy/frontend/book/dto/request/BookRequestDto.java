package com.nhnacademy.frontend.book.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.frontend.category.dto.CategoryInfoResponseDto;
import com.nhnacademy.frontend.category.dto.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {
    @JsonProperty("book_isbn")
    private String bookIsbn;

    @JsonProperty("book_title")
    private String bookTitle;

    @JsonProperty("book_description")
    private String bookDescription;

    @JsonProperty("book_published_at")
    private LocalDate publishedAt;

    @JsonProperty("book_publisher")
    private String publisher;

    @JsonProperty("book_fixed_price")
    private Long bookFixedPrice;

    @JsonProperty("book_sale_price")
    private Long bookSalePrice;

    @JsonProperty("book_is_packing")
    private boolean bookIsPacking;

    @JsonProperty("book_views")
    private Long bookViews;

    @JsonProperty("book_status")
    private int bookStatus;

    @JsonProperty("book_quantity")
    private int bookQuantity;

    @JsonProperty("book_image")
    private String bookImage;

    @JsonProperty("author")
    private String author;
//    private List<BookTagResponseDto> tags;
//    private List<CategoryResponseDto> categories;
    private Long likes;
}
