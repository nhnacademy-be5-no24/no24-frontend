package com.nhnacademy.frontend.main.cartOrder.domain;

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
@RedisHash(value = "cartOrder")
public class CartOrder {
    @Id
    private Long customerNo;

    private List<Book> books = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Book implements Serializable {
        @JsonProperty("book_isbn")
        private String isbn;

        @JsonProperty("book_quantity")
        private Long quantity;  // 도서 구매 수량
    }
}