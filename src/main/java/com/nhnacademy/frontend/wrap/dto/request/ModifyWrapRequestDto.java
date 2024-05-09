package com.nhnacademy.frontend.wrap.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포장 수정을 위한 request dto 입니다.
 *
 * @author : 박동희
 * @date : 2024-03-29
 **/

@NoArgsConstructor
@Builder
@Getter
public class ModifyWrapRequestDto {
    private Long wrapId;
    private String wrapName;
    private Long wrapCost;

    public ModifyWrapRequestDto(Long wrapId, String wrapName, Long wrapCost) {
        this.wrapId = wrapId;
        this.wrapName = wrapName;
        this.wrapCost = wrapCost;
    }
}
