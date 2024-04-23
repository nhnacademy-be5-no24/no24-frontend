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
@RequestMapping("/order")
@RequiredArgsConstructor
public class CartOrderController {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * [POST /order/create/redis]
     * FE에서 선택한 상품만 받아와 주문 내역에 등록하는 Post 메소드
     */
    @PostMapping("/create/redis")
    public ResponseEntity<String> addToCart(@RequestHeader("customerId") String customerId, @RequestBody List<CartRequestDto> cartRequestDtoList) {
        HashOperations<String, String, CartOrder> hashOperations = redisTemplate.opsForHash();
        CartOrder order = new CartOrder();
        order.setCustomerId(customerId);

        for (CartRequestDto cartRequestDto : cartRequestDtoList) {
            CartOrder.Book book = CartOrder.Book.builder()
                    .isbn(cartRequestDto.getBookIsbn())
                    .quantity(cartRequestDto.getBookQuantity())
                    .build();
            order.getBooks().add(book);
        }

        hashOperations.put("order", customerId, order);

        return ResponseEntity.ok("주문 내역에 " + customerId + "의 주문이 추가되었습니다.");
    }

    /**
     * [DELETE /order/delete/redis/{customerId}]
     * 결제 완료 후, redis에서 customerId의 주문 내역을 삭제하는 DELETE 메서드
     */
    @DeleteMapping("/delete/redis/{customerId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String customerId) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        if (hashOperations.hasKey("order", customerId)) {
            hashOperations.delete("order", customerId);
            return ResponseEntity.ok(customerId + "의 주문 내역이 삭제되었습니다.");
        } else {
            throw new CartNotFoundException(customerId);
        }
    }
}
