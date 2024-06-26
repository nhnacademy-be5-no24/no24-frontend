package com.nhnacademy.frontend.main.cartOrder.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderRequestDto {
    @JsonProperty("book_isbn")
    private String bookIsbn;

    @JsonProperty("book_quantity")
    private Long bookQuantity;
}
