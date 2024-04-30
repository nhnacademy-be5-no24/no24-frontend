package com.nhnacademy.frontend.main.cartOrder.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CartPaymentRequestDto {
    private List<BookInfo> bookInfos;
    private Long customerNo;

    @Getter
    @Builder
    public static class BookInfo{
        private String bookIsbn;
        private Long bookSalePrice;
        private Long quantity;
    }
}
