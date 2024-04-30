package com.nhnacademy.frontend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberCreateRequest {
    @NotBlank(message = "사용할 아이디를 입력해주세요")
    private String customerId;
    @NotBlank(message = "사용할 비밀번호를 입력해주세요")
    private String customerPassword;
    @NotBlank(message = "이름을 입력해주세요")
    private String customerName;
    private String customerPhoneNumber;
    @Email
    private String customerEmail;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate customerBirthday;
    private String customerPostcode;
    private String customerAddress;
    private String customerDetailAddress;
}
