package com.nhnacademy.frontend.main.order.dto.request;

import lombok.*;

import java.util.List;

/**
 * ItemDto (temp)
 * todo: Book으로 수정이 필요함
 *
 * @Author : 박병휘
 * @Date : 2024/04/10
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderDetailDto {
    String bookIsbn;
    Long quantity;
    Long price;
    Long couponId;
    List<WrapDto> wraps;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrapDto {
        Long wrapId;
        Long quantity;
    }
}
