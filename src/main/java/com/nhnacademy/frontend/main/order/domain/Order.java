package com.nhnacademy.frontend.main.order.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@RedisHash(value = "order")
public class Order {
    @Id

}
