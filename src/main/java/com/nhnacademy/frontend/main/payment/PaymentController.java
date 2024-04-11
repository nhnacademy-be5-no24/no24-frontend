package com.nhnacademy.frontend.main.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import com.nhnacademy.frontend.item.ItemDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.*;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

/**
 * 결제 Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/10
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {
    @PostMapping
    public ModelAndView getPaymentPage(HttpServletRequest request) {
        Gson gson = new Gson();
        List<ItemDto> itemList = new ArrayList<>();
        Set<String> itemIdList = new HashSet<>();

        for (Iterator<String> it = request.getParameterNames().asIterator(); it.hasNext(); ) {
            String name = it.next();
            String itemId = name.split("-")[name.split("-").length - 1];

            itemIdList.add(itemId);
        }

        for(String itemId : itemIdList) {
            String wrapJson = request.getParameter("wrap-info-" + itemId);
            List<ItemDto.Wrap> wraps = new ArrayList<>();

            if(wrapJson != null && wrapJson.length() != 0) {
                wraps = gson.fromJson(wrapJson, new TypeToken<ArrayList<ItemDto.Wrap>>() {}.getType());
            }


            for (ItemDto.Wrap wrap : wraps) {
                System.out.println("Wrap ID: " + wrap
                        .getWrapId());
                System.out.println("Quantity: " + wrap.getQuantity());
            }

            ItemDto item = ItemDto.builder()
                    .itemId(itemId)
                    .quantity(Long.parseLong(request.getParameter("quantity-" + itemId)))
                    .couponId(Long.parseLong(request.getParameter("coupon-" + itemId)))
                    .wraps(wraps)
                    .build();

            itemList.add(item);

            // todo: 총 금액 계산 Logic 필요. (Shop service 사용)
        }

        String orderId = generateRandomString();

        try {
            saveOrderInRedis(orderId, itemList);
        } catch(JsonProcessingException e) {
            System.out.println("error: " + e);
        }

        // todo: 필요내용 추가 필요
        ModelAndView mav = new ModelAndView("index/main/order/payment");
        mav.addObject("orderId", orderId);
        mav.addObject("orderTitle", "example1 외 5권");
        mav.addObject("amount", 50000);
        mav.addObject("customerEmail", "example@gmail.com");
        mav.addObject("customerName", "홍길동");
        mav.addObject("customerMobilePhone", "01012345678");

        return mav;
    }

    /**
     * Save Order Information in Redis
     * @Param String orderId
     * @Param List<ItemDto> itemDtoList
     */
    public void saveOrderInRedis(String orderId, List<ItemDto> itemDtoList) throws JsonProcessingException {
        // Redis 서버에 연결
        Jedis jedis = new Jedis("133.186.223.228", 6379);
        jedis.auth("*N2vya7H@muDTwdNMR!"); // 비밀번호 인증

        jedis.select(41);

        ObjectMapper mapper = new ObjectMapper();

        // 정보 저장
        jedis.setex(orderId, 300, mapper.writeValueAsString(itemDtoList));

        // 연결 종료
        jedis.close();
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
