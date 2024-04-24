package com.nhnacademy.frontend.main.cart.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(Long customerNo) {
        super(customerNo + "의 장바구니가 존재하지 않습니다.");
    }
}
