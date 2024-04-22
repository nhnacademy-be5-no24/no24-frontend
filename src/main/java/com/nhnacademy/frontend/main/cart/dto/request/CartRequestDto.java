package com.nhnacademy.frontend.main.cart.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDto {
    @JsonProperty("book_isbn")
    private String bookIsbn;

    @JsonProperty("book_quantity")
    private Long bookQuantity;
}
