package com.nhnacademy.frontend.address.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDto {
    @JsonProperty("address_id")
    private Long addressId;

    private String alias;

    @JsonProperty("receiver_name")
    private String receiverName;

    @JsonProperty("receiver_phone_number")
    private String receiverPhoneNumber;

    private String zipcode;

    private String address;

    @JsonProperty("address_detail")
    private String addressDetail;

    @JsonProperty("is_default")
    private Boolean isDefault;
}
