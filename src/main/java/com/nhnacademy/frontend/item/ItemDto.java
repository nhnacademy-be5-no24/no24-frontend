package com.nhnacademy.frontend.item;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * ItemDto (temp)
 * todo: Book으로 수정이 필요함
 *
 * @Author : 박병휘
 * @Date : 2024/04/10
 */
@Data
@Builder
public class ItemDto {
    String itemId;
    Long quantity;
    Long couponId;
    List<Wrap> wraps;

    @Getter
    @Data
    public class Wrap {
        int wrapId;
        int quantity;
    }
}
