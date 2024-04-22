package com.nhnacademy.frontend.main.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.common.utils.CookieUtils;
import com.nhnacademy.frontend.main.cart.domain.Cart;
import com.nhnacademy.frontend.main.cart.dto.request.CartRequestDto;
import com.nhnacademy.frontend.main.cart.dto.response.CartResponseDto;
import com.nhnacademy.frontend.main.cart.exception.CartNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart Controller
 *
 * @Author: jinjiwon
 * @Date: 17/04/2024
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final RedisTemplate<String, Object> redisTemplate;
//    private final CookieUtils cookieUtils;
    private static final ObjectMapper mapper = new ObjectMapper();

    // todo 0: customerId는 cookie를 통해서 가져올 예정

    /**
     * [POST /cart/create]
     * 장바구니에 상품을 추가하는 Post 메소드
     */
    // todo: 비회원 시, redis 유효시간 3시간 설정 추가
    @PostMapping("/create")
    public ResponseEntity<String> addToCart(@RequestHeader("customerId") String customerId, @RequestBody CartRequestDto cartRequestDto) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();
        Cart cart = hashOperations.get("cart", customerId);

        // 고객의 장바구니가 존재하는지 확인
        if (cart == null) {
            cart = new Cart();
            cart.setCustomerId(customerId);
        }

        // 장바구니에 동일한 상품이 있는지 체크
        boolean isExistingItem = false;

        for (Cart.Book existingBook : cart.getBooks()) {
            if (existingBook.getIsbn().equals(cartRequestDto.getBookIsbn())) {
                existingBook.setQuantity(existingBook.getQuantity() + cartRequestDto.getBookQuantity());
                isExistingItem = true;
                break;
            }
        }

        if (!isExistingItem) {
            Cart.Book newBook = Cart.Book.builder()
                    .isbn(cartRequestDto.getBookIsbn())
                    .quantity(cartRequestDto.getBookQuantity())
                    .build();
            cart.getBooks().add(newBook);
        }

        hashOperations.put("cart", customerId, cart);

        return ResponseEntity.ok(customerId + "의 장바구니에 " + cartRequestDto.getBookIsbn() + "이/가 " + cartRequestDto.getBookQuantity() + "개 추가되었습니다.");
    }
}
