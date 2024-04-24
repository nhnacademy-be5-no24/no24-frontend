package com.nhnacademy.frontend.main.cartOrder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.main.cart.domain.Cart;
import com.nhnacademy.frontend.main.cart.exception.CartNotFoundException;
import com.nhnacademy.frontend.main.cartOrder.domain.CartOrder;
import com.nhnacademy.frontend.main.cartOrder.dto.request.CartOrderRequestDto;
import com.nhnacademy.frontend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/cartOrder")
@RequiredArgsConstructor
public class CartOrderController {
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * [POST /cartOrder/create]
     * FE에서 선택한 상품만 받아와 주문 내역에 등록하는 Post 메소드
     */
    @PostMapping("/create")
    public ResponseEntity<String> addToCart(HttpServletRequest request,
                                            @RequestBody List<CartOrderRequestDto> cartOrderRequestDtoList) {
        Long customerNo = AuthUtil.getCustomerNo(
                requestUrl,
                port,
                request,
                redisTemplate,
                restTemplate
        );

        HashOperations<String, String, CartOrder> hashOperations = redisTemplate.opsForHash();
        CartOrder order = new CartOrder();
        order.setCustomerNo(customerNo.toString());

        for (CartOrderRequestDto cartOrderRequestDto : cartOrderRequestDtoList) {
            CartOrder.Book book = CartOrder.Book.builder()
                    .isbn(cartOrderRequestDto.getBookIsbn())
                    .quantity(cartOrderRequestDto.getBookQuantity())
                    .build();
            order.getBooks().add(book);
        }

        hashOperations.put("cartOrder", String.valueOf(customerNo), order);

        return ResponseEntity.ok("주문 내역에 " + customerNo + "의 주문이 추가되었습니다.");
    }

    /**
     * [DELETE /cartOrder/delete/{customerNo}]
     * 결제 완료 후, redis에서 customerId의 주문 내역을 삭제하는 DELETE 메서드
     */
    @DeleteMapping("/delete/{customerNo}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long customerNo) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        if (hashOperations.hasKey("cartOrder", customerNo)) {
            hashOperations.delete("cartOrder", customerNo);
            return ResponseEntity.ok(customerNo + "의 주문 내역이 삭제되었습니다.");
        } else {
            throw new CartNotFoundException(customerNo);
        }
    }
}
