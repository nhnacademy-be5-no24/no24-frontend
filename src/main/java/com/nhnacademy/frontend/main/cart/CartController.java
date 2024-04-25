package com.nhnacademy.frontend.main.cart;

import com.nhnacademy.frontend.main.cart.domain.Cart;
import com.nhnacademy.frontend.main.cart.dto.CartListResponseDto;
import com.nhnacademy.frontend.main.cart.dto.CartRequestDto;
import com.nhnacademy.frontend.main.cart.dto.CartResponseDto;
import com.nhnacademy.frontend.util.AuthUtil;
import com.nhnacademy.frontend.util.exception.NotFoundToken;
import com.nhnacademy.frontend.util.exception.UnauthorizedTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/08
 */
@Controller
public class CartController {
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    public CartController(RedisTemplate redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    /**
     * getCartPage
     * @return mav
     */
    @GetMapping("/cart")
    public ModelAndView getCartPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/main/cart/cart");

        try {
            Long customerNo = AuthUtil.getCustomerNo(
                    requestUrl,
                    port,
                    request,
                    redisTemplate,
                    restTemplate
            );

            ResponseEntity<CartListResponseDto> response = restTemplate.getForEntity(
                    requestUrl + ":" + port + "/shop/cart/" + customerNo,
                    CartListResponseDto.class
            );

            List<CartResponseDto> cartList = response.getBody().getCartResponseDtoList();

            if(cartList == null || cartList.isEmpty()) {
                mav.addObject("cart", new ArrayList<>());
            }
            else
                mav.addObject("cart", cartList);

        } catch(UnauthorizedTokenException | NotFoundToken e) {
            HttpSession session = request.getSession();
            String jSessionId = session.getId();

            ResponseEntity<CartListResponseDto> response = restTemplate.getForEntity(
                    requestUrl + ":" + port + "/shop/cart/" + jSessionId,
                    CartListResponseDto.class
            );


            List<CartResponseDto> cartList = response.getBody().getCartResponseDtoList();

            if(cartList == null || cartList.isEmpty()) {
                mav.addObject("cart", new ArrayList<>());
            }
            else
                mav.addObject("cart", cartList);
        }

        return mav;
    }

    @PostMapping("/cart/create")
    public ResponseEntity<String> addCart(HttpServletRequest request,
                                          @RequestBody CartRequestDto cartRequestDto) {
        try {
            Long customerNo = AuthUtil.getCustomerNo(
                    requestUrl,
                    port,
                    request,
                    redisTemplate,
                    restTemplate
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                    requestUrl + ":" + port + "/shop/cart/create/" + customerNo,
                    cartRequestDto,
                    String.class
            );

            if(response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getBody());
            }
        } catch(UnauthorizedTokenException | NotFoundToken e) {
            HttpSession session = request.getSession();
            String jSessionId = session.getId();

            ResponseEntity<String> response = restTemplate.postForEntity(
                    requestUrl + ":" + port + "/shop/cart/create/" + jSessionId,
                    cartRequestDto,
                    String.class
            );

            if(response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getBody());
            }
        }
    }

    @PutMapping("/cart/update")
    public ResponseEntity<String> updateCart(HttpServletRequest request,
                                          @RequestBody CartRequestDto cartRequestDto) {
        try {
            Long customerNo = AuthUtil.getCustomerNo(
                    requestUrl,
                    port,
                    request,
                    redisTemplate,
                    restTemplate
            );

            restTemplate.put(
                    requestUrl + ":" + port + "/shop/cart/update/" + customerNo,
                    cartRequestDto
            );
        } catch(UnauthorizedTokenException | NotFoundToken e) {
            try {
                HttpSession session = request.getSession();
                String jSessionId = session.getId();

                restTemplate.put(
                        requestUrl + ":" + port + "/shop/cart/update/" + jSessionId,
                        cartRequestDto
                );
            } catch(Exception ex) {
                return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("저장 성공");
    }

    @DeleteMapping("/cart/delete/{bookIsbn}")
    public ResponseEntity<String> deleteCart(HttpServletRequest request,
                                             @PathVariable String bookIsbn) {
        try {
            Long customerNo = AuthUtil.getCustomerNo(
                    requestUrl,
                    port,
                    request,
                    redisTemplate,
                    restTemplate
            );

            restTemplate.delete(
                    requestUrl + ":" + port + "/shop/cart/deleteOne/" + customerNo +"?bookIsbn=" + bookIsbn
            );
        } catch(UnauthorizedTokenException | NotFoundToken e) {
            try {
                HttpSession session = request.getSession();
                String jSessionId = session.getId();

                restTemplate.delete(
                        requestUrl + ":" + port + "/shop/cart/deleteOne/" + jSessionId +"?bookIsbn=" + bookIsbn
                );
            } catch(Exception ex) {
                return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("삭제 성공");
    }
}
