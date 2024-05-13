package com.nhnacademy.frontend.main.coupon;

import com.nhnacademy.frontend.category.dto.ParentCategoryResponseDto;
import com.nhnacademy.frontend.coupon.dto.response.CouponResponseDto;
import com.nhnacademy.frontend.coupon.dto.response.CouponResponseDtoList;
import com.nhnacademy.frontend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Order Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/08
 */
@Controller
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;
    private final AmqpTemplate rabbitTemplate;

    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    /**
     * getCouponPage
     * @return mav
     */
    @GetMapping
    public ModelAndView getCouponListPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/main/coupon/coupon");
        Long customerNo = AuthUtil.getCustomerNo(requestUrl, port, request, redisTemplate, restTemplate);

        ResponseEntity<CouponResponseDtoList> categoryResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/coupon/available/" + customerNo + "?pageSize=1&offset=10",
                CouponResponseDtoList.class
        );

        List<CouponResponseDto> couponResponseDtoList = categoryResponseEntity.getBody().getCouponResponseDtoList();

        mav.addObject("couponList", couponResponseDtoList);

        return mav;
    }

    @GetMapping("/issue/{couponId}")
    public ModelAndView issueCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/coupon");
        Long customerNo = AuthUtil.getCustomerNo(requestUrl, port, request, redisTemplate, restTemplate);

        try {
            String message = "coupon_id: " + couponId + ", member_id: " + customerNo;
            rabbitTemplate.convertAndSend("COUPON_QUEUE", message);
        } catch(Exception e) {
            mav.setViewName("redirect:/error");
        }

        return mav;
    }
}
