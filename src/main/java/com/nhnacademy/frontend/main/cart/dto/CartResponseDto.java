package com.nhnacademy.frontend.main.cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {
    @JsonProperty("book_isbn")
    private String bookIsbn;

    @JsonProperty("book_title")
    private String bookTitle;

    @JsonProperty("book_fixed_price")
    private Long bookFixedPrice;

    @JsonProperty("book_sale_price")
    private Long bookSalePrice;

    @JsonProperty("book_status")
    private int bookStatus;

    @JsonProperty("book_quantity")
    private int bookQuantity;  // 도서 구매 수량

    @JsonProperty("book_image")
    private String bookImage;
}
