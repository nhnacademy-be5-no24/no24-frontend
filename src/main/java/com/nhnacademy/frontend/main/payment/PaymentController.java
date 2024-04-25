package com.nhnacademy.frontend.main.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import com.nhnacademy.frontend.item.ItemDto;
import com.nhnacademy.frontend.main.order.domain.Order;
import com.nhnacademy.frontend.util.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.*;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

/**
 * 결제 Controller
 *
 * @Author : 박병휘, 진지원
 * @Date : 2024/04/10
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final RedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    public PaymentController(RedisTemplate redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ModelAndView getPaymentPage(HttpServletRequest request) {
        Long customerNo = AuthUtil.getCustomerNo(
                requestUrl,
                port,
                request,
                redisTemplate,
                restTemplate
        );

//        HashOperations<String, String, Order> hashOperations = redisTemplate.opsForHash();
//        Order order = new Order();
//        order.setCustomerNo(customerNo);
//        order.setOrderName(orderRequestDto.getFirstBookTitle() + " 외 " + orderRequestDto.getNumberOfBook() + "권");
//        order.setTotalPrice(orderRequestDto.getTotalPrice());
//        order.setDeliveryAddress(orderRequestDto.getDeliveryAddress());
//
//        hashOperations.put("order", String.valueOf(customerNo), order);
//
//        System.out.println(itemList);
//
//        String orderId = generateRandomString();
//
//        try {
//            saveOrderInRedis(jSessionId, orderId, itemList);
//        } catch(JsonProcessingException e) {
//            System.out.println("error: " + e);
//        }

        // todo: 필요내용 추가 필요
        ModelAndView mav = new ModelAndView("index/main/order/payment");
//        mav.addObject("orderId", orderId);
//        mav.addObject("orderTitle", "example1 외 5권");
//        mav.addObject("amount", 50000);
//        mav.addObject("customerEmail", "example@gmail.com");
//        mav.addObject("customerName", "홍길동");
//        mav.addObject("customerMobilePhone", "01012345678");

        return mav;

//        for (Iterator<String> it = request.getParameterNames().asIterator(); it.hasNext(); ) {
//            String name = it.next();
//            String itemId = name.split("-")[name.split("-").length - 1];
//
//            itemIdList.add(itemId);
//        }
//
//        for(String itemId : itemIdList) {
//            String wrapJson = request.getParameter("wrap-info-" + itemId);
//            List<ItemDto.Wrap> wraps = new ArrayList<>();
//
//            if(wrapJson != null && wrapJson.length() != 0) {
//                wraps = gson.fromJson(wrapJson, new TypeToken<ArrayList<ItemDto.Wrap>>() {}.getType());
//            }
//
//
//            for (ItemDto.Wrap wrap : wraps) {
//                System.out.println("Wrap ID: " + wrap
//                        .getWrapId());
//                System.out.println("Quantity: " + wrap.getQuantity());
//            }
//
//            ItemDto item = ItemDto.builder()
//                    .itemId(itemId)
//                    .quantity(Long.parseLong(request.getParameter("quantity-" + itemId)))
//                    .couponId(Long.parseLong(request.getParameter("coupon-" + itemId)))
//                    .wraps(wraps)
//                    .build();
//
//            itemList.add(item);
//
//            // todo: 총 금액 계산 Logic 필요. (Shop service 사용)
//        }
    }

    /**
     * Save Order Information in Redis
     * @Param String orderId
     * @Param List<ItemDto> itemDtoList
     */
    public void saveOrderInRedis(String jSessionId, String orderId, List<ItemDto> itemDtoList) throws JsonProcessingException {
        HashOperations<String, String, List<ItemDto>> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(jSessionId, orderId, itemDtoList);
    }


    /**
     * generate random string for orderId
     * @return String randomString
     */

    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private static final int ORDER_ID_LENGTH = 20; // orderId의 길이

    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder orderIdBuilder = new StringBuilder();

        // ORDER_ID_LENGTH 길이의 랜덤한 문자열 생성
        for (int i = 0; i < ORDER_ID_LENGTH; i++) {
            // ALLOWED_CHARACTERS 문자열에서 랜덤하게 문자 선택하여 orderId에 추가
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            orderIdBuilder.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }

        return orderIdBuilder.toString();
    }
}
