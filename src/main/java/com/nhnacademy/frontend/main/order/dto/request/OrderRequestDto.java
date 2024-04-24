package com.nhnacademy.frontend.main.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    @JsonProperty("first_book_title")
    private String firstBookTitle;

    @JsonProperty("number_of_book")
    private int numberOfBook;

    @JsonProperty("total_price")
    private Long totalPrice;

    @JsonProperty("delivery_address")
    private String deliveryAddress;
}
