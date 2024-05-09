package com.nhnacademy.frontend.wrap.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포장 등록을 위한 request dto 입니다.
 *
 * @author : 박동희
 * @date : 2024-03-29
 **/
@Getter
@NoArgsConstructor
@Builder
public class WrapRequestDto {
    private String wrapName;
    private Long wrapCost;

    public WrapRequestDto(String wrapName, Long wrapCost) {
        this.wrapName = wrapName;
        this.wrapCost = wrapCost;
    }
}
