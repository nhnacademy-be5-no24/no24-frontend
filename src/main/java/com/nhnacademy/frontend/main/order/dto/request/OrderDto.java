package com.nhnacademy.frontend.main.order.dto.request;

import com.nhnacademy.frontend.main.cart.dto.CartListResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@RedisHash(value = "order")
public class OrderDto implements Serializable {
    private String orderId;
    private String orderName;

    private String customerNo;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerName;

    private String receiverName;
    private String receiverPhoneNumber;

    private Long totalPrice;
    private int deliveryFee;
    private String postcode;
    private String address;
    private String addressDetail;
    private List<OrderDetailDto> OrderDetailDto;
}
