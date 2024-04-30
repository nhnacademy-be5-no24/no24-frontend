package com.nhnacademy.frontend.address.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDtoList {
    List<AddressResponseDto> addressResponseDtoList;
}
