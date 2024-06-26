package com.nhnacademy.frontend.main.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDto {
    private String bookIsbn;
    private int bookQuantity;
}
