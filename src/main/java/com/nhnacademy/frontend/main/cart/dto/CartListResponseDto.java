package com.nhnacademy.frontend.main.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartListResponseDto {
    List<CartResponseDto> cartResponseDtoList;
}
