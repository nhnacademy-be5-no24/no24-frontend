package com.nhnacademy.frontend.main.payment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhnacademy.frontend.book.dto.BookResponseDto;
import com.nhnacademy.frontend.coupon.dto.CouponType;
import com.nhnacademy.frontend.coupon.dto.response.CouponResponseDto;
import com.nhnacademy.frontend.main.order.dto.request.OrderDetailDto;
import com.nhnacademy.frontend.main.order.dto.request.OrderDto;
import com.nhnacademy.frontend.util.AuthUtil;
import com.nhnacademy.frontend.util.exception.NotFoundToken;
import com.nhnacademy.frontend.util.exception.UnauthorizedTokenException;
import com.nhnacademy.frontend.wrap.dto.response.WrapResponseDto;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        ModelAndView mav = new ModelAndView("index/main/order/payment");
        HashOperations<String, String, OrderDto> hashOperations = redisTemplate.opsForHash();
        String orderId = generateRandomString();

        try {
            Long customerNo = AuthUtil.getCustomerNo(
                    requestUrl,
                    port,
                    request,
                    redisTemplate,
                    restTemplate
            );

            OrderDto orderDto = createOrder(request, customerNo.toString());
            mav.addObject("order", orderDto);

            hashOperations.put("order", String.valueOf(customerNo), orderDto);

            // 만료 시간 설정 (10분)
            redisTemplate.expire("order-" + customerNo, 10, TimeUnit.MINUTES);

        } catch(UnauthorizedTokenException | NotFoundToken e) {
            try {
                HttpSession session = request.getSession();
                String jSessionId = session.getId();

                OrderDto orderDto = createOrder(request, jSessionId);
                mav.addObject("order", orderDto);

                hashOperations.put("order", jSessionId, orderDto);

                // 만료 시간 설정 (10분)
                redisTemplate.expire("order-" + jSessionId, 10, TimeUnit.MINUTES);
            } catch(Exception ex) {
                mav.setViewName("index/error");
                return mav;
            }
        } catch(Exception ex) {
            mav.setViewName("index/error");
        }

        return mav;
    }

    /**
     * 주문 정보를 만드는 메소드
     * @param request
     * @param customerNo
     * @return
     * @throws Exception (BookIsbn 검색이 안되는 경우, 예외처리)
     */
    public OrderDto createOrder(HttpServletRequest request, String customerNo) throws Exception {
        List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();
        Set<String> isbnList = new HashSet<>();
        Gson gson = new Gson();
        String firstBookName = "";
        Long totalPrice = 0L;

        for (Iterator<String> it = request.getParameterNames().asIterator(); it.hasNext(); ) {
            String name = it.next();

            if(!name.contains("-")) continue;

            String itemId = name.split("-")[name.split("-").length - 1];

            isbnList.add(itemId);
        }
        try {
            int i = 0;
            for (String isbn : isbnList) {
                // 책 가격 수집
                ResponseEntity<BookResponseDto> bookResponseEntity = restTemplate.getForEntity(
                        requestUrl + ":" + port + "/shop/books/isbn/" + isbn,
                        BookResponseDto.class
                );

                Long price = bookResponseEntity.getBody().getBookSalePrice();
                if (i == 0) firstBookName = bookResponseEntity.getBody().getBookTitle();

                String wrapJson = request.getParameter("wrap-info-" + isbn);
                List<OrderDetailDto.WrapDto> wraps = new ArrayList<>();

                if (wrapJson != null && wrapJson.length() != 0) {
                    wraps = gson.fromJson(wrapJson, new TypeToken<ArrayList<OrderDetailDto.WrapDto>>() {
                    }.getType());
                }


                OrderDetailDto orderDetailDto = OrderDetailDto.builder()
                        .bookIsbn(isbn)
                        .quantity(Long.parseLong(request.getParameter("quantity-" + isbn)))
                        .price(price)
                        .couponId(Long.parseLong(request.getParameter("coupon-" + isbn)))
                        .wraps(wraps)
                        .build();

                orderDetailDtoList.add(orderDetailDto);

                // book price 계산
                long bookPrice = (orderDetailDto.getPrice() * orderDetailDto.getQuantity());

                // 포장지 계산
                for (OrderDetailDto.WrapDto wrap : wraps) {
                    ResponseEntity<WrapResponseDto> wrapResponseEntity = restTemplate.getForEntity(
                            requestUrl + ":" + port + "/shop/wraps/id/" + wrap.getWrapId(),
                            WrapResponseDto.class
                    );
                    Long cost = wrapResponseEntity.getBody().getWrapCost();

                    bookPrice += (cost * wrap.getQuantity());
                }

                if(orderDetailDto.getCouponId() != 0L) {
                    // 쿠폰 계산
                    ResponseEntity<Long> couponIdResponseEntity = restTemplate.getForEntity(
                            requestUrl + ":" + port + "/shop/coupon/member/couponMemberId/" + orderDetailDto.getCouponId(),
                            Long.class
                    );

                    ResponseEntity<CouponResponseDto> couponResponseEntity = restTemplate.getForEntity(
                            requestUrl + ":" + port + "/shop/coupon/" + couponIdResponseEntity.getBody(),
                            CouponResponseDto.class
                    );
                    CouponResponseDto couponResponseDto = couponResponseEntity.getBody();

                    if (couponResponseDto.getCouponType() == CouponType.AMOUNT) {
                        bookPrice -= couponResponseDto.getDiscountPrice();
                    } else {
                        long discountBookPrice = bookPrice * couponResponseDto.getDiscountRate() / 100;
                        discountBookPrice = Math.min(discountBookPrice, couponResponseDto.getMaxDiscountPrice());

                        bookPrice -= discountBookPrice;
                    }
                }

                totalPrice += bookPrice;
                i++;
            }
        } catch(Exception e) {
            throw new Exception("book is not found");
        }

        int deliveryFee = 3000;
        totalPrice -= Long.parseLong(request.getParameter("usedPoint"));

        if(totalPrice >= 30000L) {
            deliveryFee = 0;
        }

        totalPrice += deliveryFee;

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(generateRandomString());
        orderDto.setShipDate(LocalDate.parse(request.getParameter("shipDate"), DateTimeFormatter.ISO_DATE));
        orderDto.setCustomerNo(customerNo);
        orderDto.setCustomerName(request.getParameter("customerName"));
        orderDto.setCustomerEmail(request.getParameter("customerEmail"));
        orderDto.setCustomerPhoneNumber(request.getParameter("customerPhoneNumber"));
        orderDto.setCustomerName(request.getParameter("customerName"));
        orderDto.setReceiverName(request.getParameter("receiverName"));
        orderDto.setReceiverPhoneNumber(request.getParameter("receiverPhoneNumber"));
        orderDto.setOrderName(firstBookName + " 외 " + (isbnList.size() - 1) + "건");
        orderDto.setTotalPrice(totalPrice);
        orderDto.setUsedPoint(Long.parseLong(request.getParameter("usedPoint")));
        orderDto.setDeliveryFee(deliveryFee);
        orderDto.setPostcode(request.getParameter("receiverPostcode"));
        orderDto.setAddress(request.getParameter("receiverAddress"));
        orderDto.setAddressDetail(request.getParameter("receiverDetailAddress"));
        orderDto.setOrderDetailDto(orderDetailDtoList);

        return orderDto;
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
