package com.nhnacademy.frontend.main.order.dto.response;

import com.nhnacademy.frontend.wrap.dto.response.WrapResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 포장지 리스트를 가져오기 위한 dto
 *
 * @Author : 박병휘
 * @Date : 2024/04/26
 */
@Getter
@NoArgsConstructor
@Builder
public class WrapResponseDtoList {
    List<WrapResponseDto> wrapResponseDtos;

    WrapResponseDtoList(List<WrapResponseDto> wrapResponseDtoList) {
        this.wrapResponseDtos = wrapResponseDtoList;
    }
}
