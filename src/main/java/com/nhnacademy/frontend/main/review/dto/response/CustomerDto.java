package com.nhnacademy.frontend.main.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private String customerId;
    private String customerPassword;
    private String customerName;
    private String customerPhoneNumber;
    private String customerEmail;
    private LocalDate customerBirthday;
    private String customerRole;
}
