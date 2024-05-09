package com.nhnacademy.frontend.main.order.dto.response;

import com.nhnacademy.frontend.book.dto.BookResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class OrderDetailResponseDto {
    BookResponseDto book;
    Long quantity;
    List<WrapDto> wraps;

    public Long getWrapsPrice() {
        return this.getWraps().stream().mapToLong(wrap -> wrap.quantity * wrap.getWrapCost()).sum();
    }

    @Data
    @NoArgsConstructor
    @Builder
    public static class WrapDto {
        String wrapName;
        Long wrapCost;
        Long quantity;

        public WrapDto(String wrapName, Long wrapCost, Long quantity) {
            this.wrapName = wrapName;
            this.wrapCost = wrapCost;
            this.quantity = quantity;
        }
    }
}