package com.nhnacademy.frontend.main.order;


import com.nhnacademy.frontend.book.dto.BookResponseDto;
import com.nhnacademy.frontend.main.cartOrder.dto.request.CartPaymentRequestDto;
import com.nhnacademy.frontend.main.cartOrder.dto.response.CartPaymentResponseDto;
import com.nhnacademy.frontend.main.order.dto.request.OrderDetailDto;
import com.nhnacademy.frontend.main.order.dto.request.OrderDto;
import com.nhnacademy.frontend.main.order.dto.request.OrdersCreateRequestDto;
import com.nhnacademy.frontend.main.order.dto.response.WrapResponseDtoList;
import com.nhnacademy.frontend.util.AuthUtil;

import com.nhnacademy.frontend.util.exception.NotFoundToken;
import com.nhnacademy.frontend.util.exception.UnauthorizedTokenException;
import com.nhnacademy.frontend.wrap.dto.WrapResponseDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

/**
 * Order Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/08
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final RedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    // todo: delete example Class
    private class Item {
        public String id;
        public String title;
        public Long quantity;
        public Long price;
        public Long discount;

        public Item(String id, String title, Long quantity, Long price, Long discount) {
            this.id = id;
            this.title = title;
            this.quantity = quantity;
            this.price = price;
            this.discount = discount;
        }
    }

    public OrderController(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * getOrderPage
     * @return mav
     */
    @PostMapping
    public ModelAndView getOrderPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/main/order/order");

        Long customerNo = AuthUtil.getCustomerNo(
                requestUrl,
                port,
                request,
                redisTemplate,
                restTemplate
        );

        List<CartPaymentRequestDto.BookInfo> bookInfos = new ArrayList<>();

        // Book Isbn과 개수 추출
        // Book Info 저장.
        for (Iterator<String> it = request.getParameterNames().asIterator(); it.hasNext(); ) {
            String field = it.next();

            if(field.contains("item-checkbox-")) {
                String bookIsbn = field.split("-")[2];

                // book info 수집.
                ResponseEntity<BookResponseDto> bookResponseEntity = restTemplate.getForEntity(
                        requestUrl + ":" + port +  "/shop/books/isbn/" + bookIsbn,
                        BookResponseDto.class
                );

                BookResponseDto bookResponseDto = bookResponseEntity.getBody();

                CartPaymentRequestDto.BookInfo bookInfo = CartPaymentRequestDto.BookInfo.builder()
                        .bookIsbn(bookIsbn)
                        .quantity(Long.parseLong(request.getParameter("quantity-" + bookIsbn)))
                        .bookSalePrice(bookResponseDto.getBookSalePrice())
                        .build();

                bookInfos.add(bookInfo);
            }
        }

        CartPaymentRequestDto cartPaymentRequestDto = CartPaymentRequestDto.builder()
                .bookInfos(bookInfos)
                .customerNo(customerNo)
                .build();

        // 주문 정보 수집.
        ResponseEntity<CartPaymentResponseDto> response = restTemplate.postForEntity(
                requestUrl + ":" + port +  "/shop/orders/cart",
                cartPaymentRequestDto,
                CartPaymentResponseDto.class
        );
        CartPaymentResponseDto cartInfo = response.getBody();
        System.out.println(cartInfo);

        ResponseEntity<Long> point = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/point/" + customerNo,
                Long.class);

        mav.addObject("cartInfo", cartInfo);
        mav.addObject("tomorrow", LocalDate.now().plusDays(1));
        mav.addObject("point", point.getBody());

        return mav;
    }

    @GetMapping("/input/wrap/{bookIsbn}")
    public ModelAndView getWrapPopupPage(@PathVariable String bookIsbn) {
        ModelAndView mav = new ModelAndView("index/main/order/input_wrap");

        ResponseEntity<WrapResponseDtoList> response = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/wraps?page=0&size=10",
                WrapResponseDtoList.class
        );

        List<WrapResponseDto> wrapList = response.getBody().getWrapResponseDtos();

        mav.addObject("wrapList", wrapList);

        return mav;
    }

    @GetMapping("/payment/attempt")
    public ModelAndView getAttemptPage() {
        ModelAndView mav = new ModelAndView("index/main/order/payment_attempt");

        return mav;
    }

    @GetMapping("/success")
    public ModelAndView getSuccessPage(@RequestParam String orderId) {
        ModelAndView mav = new ModelAndView("index/main/order/success");

        mav.addObject("orderId", orderId);

        return mav;
    }

    @GetMapping("/fail")
    public ModelAndView getFailPage() {
        ModelAndView mav = new ModelAndView("index/main/order/fail");

        return mav;
    }

    @PostMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody, HttpServletRequest request) throws Exception {
        HashOperations<String, String, OrderDto> hashOperations = redisTemplate.opsForHash();
        HttpSession session = request.getSession();
        String jSessionId = session.getId();
        Long customerNo = null;
        ResponseEntity<OrdersCreateRequestDto> response = null;

        try {
            customerNo = AuthUtil.getCustomerNo(
                    requestUrl,
                    port,
                    request,
                    redisTemplate,
                    restTemplate
            );
        } catch(NotFoundToken | UnauthorizedTokenException e) {}


        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;

        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");

            OrderDto orderDto = null;

            // redis에 저장된 주문 정보를 가져오는 로직.
            if(customerNo != null)
                orderDto = hashOperations.get("order", customerNo.toString());
            else
                orderDto = hashOperations.get("order", jSessionId);

            if(orderDto == null) {
                throw new RuntimeException("Can not load information");
            }


            // todo: 주문 정보 확인 후 Shop service에서 쿠폰 사용 이력, 주문 이력, 책 수량, 포인트 적립 등 로직 수행 필요
            // todo: 로직 수행 이후, redis 내 orderId에 해당하는 정보 지우기.
            if(customerNo != null)
                hashOperations.delete("order", customerNo.toString());
            else
                hashOperations.delete("order", jSessionId);

            // shop service에서 order 저장 요청.
            OrdersCreateRequestDto ordersCreateRequestDto = OrdersCreateRequestDto.builder()
                    .orderId(orderDto.getOrderId())
                    .orderDate(LocalDateTime.now())
                    .orderState(OrdersCreateRequestDto.OrderState.COMPLETE_PAYMENT)
                    .shipDate(orderDto.getShipDate())
                    .totalFee(orderDto.getTotalPrice())
                    .deliveryFee(orderDto.getDeliveryFee())
                    .paymentId(1L)
                    .customerNo(customerNo)
                    .jSessionId(jSessionId)
                    .receiverName(orderDto.getReceiverName())
                    .receiverPhoneNumber(orderDto.getReceiverPhoneNumber())
                    .zipcode(orderDto.getPostcode())
                    .address(orderDto.getAddress())
                    .addressDetail(orderDto.getAddressDetail())
                    .req("")
                    .orderDetailDtoList(orderDto.getOrderDetailDto())
                    .usedPoint(orderDto.getUsedPoint())
                    .build();


            response = restTemplate.postForEntity(
                    requestUrl + ":" + port + "/shop/orders",
                    ordersCreateRequestDto,
                    OrdersCreateRequestDto.class
            );

            // 카트 정보 지우기
            for(OrderDetailDto orderDetailDto : orderDto.getOrderDetailDto()) {
                restTemplate.delete(requestUrl + ":" + port +
                        "/shop/cart/deleteOne/" + customerNo + "?bookIsbn=" + orderDetailDto.getBookIsbn());
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        };
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        responseStream.close();

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("orderId", response.getBody().getOrderId());
        jsonObject.put("orderDate", response.getBody().getOrderDate());
        jsonObject.put("orderState", response.getBody().getOrderState());
        jsonObject.put("shipDate", response.getBody().getShipDate());
        jsonObject.put("deliveryFee", response.getBody().getDeliveryFee());
        jsonObject.put("paymentId", response.getBody().getPaymentId());
        jsonObject.put("customerNo", response.getBody().getCustomerNo());
        jsonObject.put("jSessionId", response.getBody().getJSessionId());
        jsonObject.put("receiverName", response.getBody().getReceiverName());
        jsonObject.put("receiverPhoneNumber", response.getBody().getReceiverPhoneNumber());
        jsonObject.put("zipcode", response.getBody().getZipcode());
        jsonObject.put("address", response.getBody().getAddress());
        jsonObject.put("addressDetail", response.getBody().getAddressDetail());
        jsonObject.put("req", response.getBody().getReq());

        return ResponseEntity.status(code).body(jsonObject);
    }


    public String getOrderInRedis(String jSessionId, String orderId) {
        String value = (String) redisTemplate.opsForHash().get(jSessionId, orderId);

        return value;
    }

    public void deleteOrderInRedis(String jSessionId, String orderId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.delete(jSessionId, orderId);
    }

    /**
     * [POST /order/create]
     * todo: moved to paymentcontroller
     * 주문 내역을 redis에 저장하는 POST 메서드
     */
//    @PostMapping("/create")
//    public ResponseEntity<String> saveOrder(@RequestBody OrderRequestDto orderRequestDto, HttpServletRequest request) {
//        Long customerNo = AuthUtil.getCustomerNo(
//                requestUrl,
//                port,
//                request,
//                redisTemplate,
//                restTemplate
//        );
//
//        HashOperations<String, String, Order> hashOperations = redisTemplate.opsForHash();
//        Order order = new Order();
//        order.setCustomerNo(customerNo);
//        order.setOrderName(orderRequestDto.getFirstBookTitle() + " 외 " + orderRequestDto.getNumberOfBook() + "권");
//        order.setTotalPrice(orderRequestDto.getTotalPrice());
//        order.setDeliveryAddress(orderRequestDto.getDeliveryAddress());
//
//        hashOperations.put("order", String.valueOf(customerNo), order);
//
//        return ResponseEntity.ok(customerNo + "의 주문 내역이 추가되었습니다.");
//    }
}
