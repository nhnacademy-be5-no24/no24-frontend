package com.nhnacademy.frontend.main.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@RedisHash(value = "order")
public class Order {
    @Id
    private Long customerNo;

    @JsonProperty("order_name")
    private String orderName;

    @JsonProperty("total_price")
    private Long totalPrice;

    @JsonProperty("delivery_address")
    private String deliveryAddress;
}
