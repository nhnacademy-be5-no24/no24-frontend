package com.nhnacademy.shop.cart.dto.response;

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
    @JsonProperty("cart_id")
    private Long cartId;

    @JsonProperty("book_isbn")
    private String bookIsbn;

    @JsonProperty("book_title")
    private String bookTitle;

    @JsonProperty("book_fixed_price")
    private int bookFixedPrice;

    @JsonProperty("book_sale_price")
    private int bookSalePrice;

    @JsonProperty("book_status")
    private int bookStatus;

//    @JsonProperty("book_quantity")
//    private Long bookQuantity;

    @JsonProperty("book_image")
    private String bookImage;

    @JsonProperty("customer_no")
    private Long customerNo;
}
