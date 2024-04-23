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

    /**
     * [PUT /cart/update/{customerId}]
     * 사용자의 action으로 장바구니 상품 수량이 변경되는 경우
     */
    @PutMapping("/update/{customerId}")
    public ResponseEntity<String> updateCart(@PathVariable String customerId, @RequestBody CartRequestDto cartRequestDto) {
        String bookIsbn = cartRequestDto.getBookIsbn();
        Long newQuantity = cartRequestDto.getBookQuantity();

        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();
        Cart cart = hashOperations.get("cart", customerId);

        if (cart != null) {
            for (Cart.Book book : cart.getBooks()) {
                if (book.getIsbn().equals(bookIsbn)) {
                    book.setQuantity(newQuantity);
                    break;
                }
            }

            hashOperations.put("cart", customerId, cart);
            return ResponseEntity.ok("장바구니 상품 수량이 성공적으로 업데이트 되었습니다.");
        } else {
            throw new CartNotFoundException(customerId);
        }
    }

    /**
     * [DELETE /cart/deleteOne/{customerId}]
     * 사용자의 action으로 장바구니 내 '하나'의 상품 삭제
     */
    @DeleteMapping("/deleteOne/{customerId}")
    public ResponseEntity<String> deleteCartBook(@PathVariable String customerId, @RequestParam String bookIsbn) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        if (hashOperations.hasKey("cart", customerId)) {
            Cart cart = hashOperations.get("cart", customerId);
            cart.getBooks().removeIf(book -> book.getIsbn().equals(bookIsbn));
            hashOperations.put("cart", customerId, cart);
            return ResponseEntity.ok(bookIsbn + "이 장바구니에서 삭제되었습니다.");
        } else {
            throw new CartNotFoundException(customerId);
        }
    }

    // todo-2: 상품 결제 후, 장바구니 삭제 => 여기서 구현 => 구현 완료
    // todo-3 : todo-2 경우, 장바구니 상품 중 일부 구매하는 경우 고려해서 구현해야 함 !!!!!!
    /**
     * [DELETE /cart/delete/{customerId}]
     * 결제 완료 후, customerId의 장바구니를 삭제하는 DELETE 메서드
     */
    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<String> deleteCart(@PathVariable String customerId, @RequestBody List<CartRequestDto> cartRequestDtoList) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        if (hashOperations.hasKey("cart", customerId)) {
            hashOperations.delete("cart", customerId);
            return ResponseEntity.ok(customerId + "의 장바구니가 삭제되었습니다.");
        } else {
            throw new CartNotFoundException(customerId);
        }
    }

    // todo: 장바구니에서 선택한 상품을 주문 페이지에 어떤 형식으로 넘겨주는지 확인 필요 => delete 구현
    //  이렇게 넘겨지는 형식을 @RequestBody로 받아서 redis에서 구매한 상품만 삭제
}
