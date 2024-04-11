package com.nhnacademy.frontend.main.order;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    /**
     * getOrderPage
     * @return mav
     */
    @PostMapping
    public ModelAndView getOrderPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/main/order/order");

        // Item Id와 갯수 추출
        // todo: id -> Isbn으로 수정 필요
        for (Iterator<String> it = request.getParameterNames().asIterator(); it.hasNext(); ) {
            String field = it.next();

            if(field.contains("item-checkbox-")) {
                String itemId = field.split("-")[2];

                System.out.println("item id(" + itemId + ") : " + request.getParameter("quantity-" + itemId));
            }
        }

        // todo: delete example data.
        List<Item> cart = new ArrayList<>();
        cart.add(new Item("KNB001", "example item 1", 5L, 5000L, 1000L));
        cart.add(new Item("KNB002", "example item 2", 5L, 5000L, 1000L));
        cart.add(new Item("KNB003", "example item 3", 5L, 5000L, 1000L));
        cart.add(new Item("KNB004", "example item 4", 5L, 5000L, 1000L));

        mav.addObject("cart", cart);

        return mav;
    }

    @GetMapping("/input/wrap")
    public ModelAndView getWrapPopupPage() {
        ModelAndView mav = new ModelAndView("index/main/order/input_wrap");

        return mav;
    }

    @GetMapping("/success")
    public ModelAndView getSuccessPage() {
        ModelAndView mav = new ModelAndView("index/main/order/success");

        return mav;
    }

    @GetMapping("/fail")
    public ModelAndView getFailPage() {
        ModelAndView mav = new ModelAndView("index/main/order/fail");

        return mav;
    }

    @PostMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {
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

            // redis에 저장된 주문 정보를 가져오는 로직.
            String value = getOrderInRedis(orderId);

            if(value == null) {
                throw new RuntimeException("Can not load information");
            }

            System.out.println(value);

            // todo: 주문 정보 확인 후 Shop service에서 쿠폰 사용 이력, 주문 이력, 책 수량, 포인트 적립 등 로직 수행 필요

            // todo: 로직 수행 이후, redis 내 orderId에 해당하는 정보 지우기.
            deleteOrderInRedis(orderId);

            System.out.println("after delete: " + getOrderInRedis(orderId));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        };
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_sk_E92LAa5PVbPYmd6veANe87YmpXyJ";
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
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        return ResponseEntity.status(code).body(jsonObject);
    }


    public String getOrderInRedis(String orderId) {
        // Redis 서버에 연결
        Jedis jedis = new Jedis("133.186.223.228", 6379);
        jedis.auth("*N2vya7H@muDTwdNMR!"); // 패스워드 설정

        jedis.select(41);

        // Redis에서 특정 키에 저장된 값 불러오기
        String value = jedis.get(orderId);

        // 연결 종료
        jedis.close();

        return value;
    }

    public void deleteOrderInRedis(String orderId) {
        // Redis 서버에 연결
        Jedis jedis = new Jedis("133.186.223.228", 6379);
        jedis.auth("*N2vya7H@muDTwdNMR!"); // 패스워드 설정

        jedis.select(41);

        // 특정 키 삭제
        jedis.del(orderId);

        // 연결 종료
        jedis.close();
    }
}
