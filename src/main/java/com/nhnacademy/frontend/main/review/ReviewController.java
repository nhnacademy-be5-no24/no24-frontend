package com.nhnacademy.frontend.main.review;

import com.nhnacademy.frontend.main.review.dto.request.CreateReviewRequestDto;
import com.nhnacademy.frontend.main.review.dto.response.ConfirmedOrderDetailDto;
import com.nhnacademy.frontend.main.review.dto.response.OrderConfirmResponseDto;
import com.nhnacademy.frontend.util.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/review")
public class ReviewController {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    public ReviewController(RestTemplate restTemplate, RedisTemplate redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping
    public ResponseEntity<String> createReview(HttpServletRequest request,
                                               @RequestBody CreateReviewRequestDto createReviewRequestDto) {
        Long customerNo = AuthUtil.getCustomerNo(
                requestUrl,
                port,
                request,
                redisTemplate,
                restTemplate
        );

        createReviewRequestDto.setCustomerNo(customerNo);

        // 리뷰 작성 가능한지 체크 필요
        boolean isConfirmed = false;

        // 고객번호가 customerNo이면서 orderState가 6(구매확정인) orderIdList
        ResponseEntity<OrderConfirmResponseDto> orderConfirmResponse = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/orders/customer/" + customerNo + "/orderConfirm",
                OrderConfirmResponseDto.class
        );

        // orderId로 bookIsbn 찾아오기
        List<String> orderIdList = orderConfirmResponse.getBody().getOrderIdList();

        for (String orderId : orderIdList) {
            ResponseEntity<ConfirmedOrderDetailDto> confirmedOrderDetailResponse = restTemplate.getForEntity(
                    requestUrl + ":" + port + "/shop/orders/detail/confirm/" + orderId,
                    ConfirmedOrderDetailDto.class
            );

            List<String> bookIsbnList = confirmedOrderDetailResponse.getBody().getBookIsbnList();

            for (String bookIsbn : bookIsbnList) {
                if (bookIsbn.equals(createReviewRequestDto.getBookIsbn())) {
                    isConfirmed = true;
                    break;
                }
            }
        }

        if (isConfirmed) {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    requestUrl + ":" + port + "/shop/reviews",
                    createReviewRequestDto,
                    String.class
            );

            return response;
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
