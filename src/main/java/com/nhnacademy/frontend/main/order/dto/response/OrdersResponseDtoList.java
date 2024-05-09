package com.nhnacademy.frontend.main.order.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
public class OrdersResponseDtoList {
    List<OrdersResponseDto> ordersResponseDtos;

    public OrdersResponseDtoList(List<OrdersResponseDto> ordersResponseDtos) {
        this.ordersResponseDtos = ordersResponseDtos;
    }
}
