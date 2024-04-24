package com.nhnacademy.frontend.main.cartOrder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.main.cart.domain.Cart;
import com.nhnacademy.frontend.main.cart.dto.request.CartRequestDto;
import com.nhnacademy.frontend.main.cart.exception.CartNotFoundException;
import com.nhnacademy.frontend.main.cartOrder.domain.CartOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartOrder")
@RequiredArgsConstructor
public class CartOrderController {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * [POST /cartOrder/create]
     * FE에서 선택한 상품만 받아와 주문 내역에 등록하는 Post 메소드
     */
    @PostMapping("/create")
    public ResponseEntity<String> addToCart(@RequestHeader("customerNo") Long customerNo, @RequestBody List<CartRequestDto> cartRequestDtoList) {
        HashOperations<String, String, CartOrder> hashOperations = redisTemplate.opsForHash();
        CartOrder order = new CartOrder();
        order.setCustomerNo(customerNo);

        for (CartRequestDto cartRequestDto : cartRequestDtoList) {
            CartOrder.Book book = CartOrder.Book.builder()
                    .isbn(cartRequestDto.getBookIsbn())
                    .quantity(cartRequestDto.getBookQuantity())
                    .build();
            order.getBooks().add(book);
        }

        hashOperations.put("order", String.valueOf(customerNo), order);

        return ResponseEntity.ok("주문 내역에 " + customerNo + "의 주문이 추가되었습니다.");
    }

    /**
     * [DELETE /cartOrder/delete/{customerNo}]
     * 결제 완료 후, redis에서 customerId의 주문 내역을 삭제하는 DELETE 메서드
     */
    @DeleteMapping("/delete/{customerNo}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long customerNo) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        if (hashOperations.hasKey("order", customerNo)) {
            hashOperations.delete("order", customerNo);
            return ResponseEntity.ok(customerNo + "의 주문 내역이 삭제되었습니다.");
        } else {
            throw new CartNotFoundException(customerNo);
        }
    }
}
