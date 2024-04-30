package com.nhnacademy.frontend.main.order.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.nhnacademy.frontend.main.cart.dto.CartListResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate shipDate;

    private String receiverName;
    private String receiverPhoneNumber;

    private Long totalPrice;
    private int deliveryFee;
    private String postcode;
    private String address;
    private String addressDetail;
    private List<OrderDetailDto> OrderDetailDto;
}
