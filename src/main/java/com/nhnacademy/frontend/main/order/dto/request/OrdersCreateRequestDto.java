package com.nhnacademy.frontend.main.order.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.frontend.payment.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OrdersCreateRequestDto {
    public enum OrderState {
        COMPLETE_PAYMENT, WAITING, SHIPPING, COMPLETED, RETURNS, ORDER_CANCELED, PURCHASE_CONFIRMED
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private String orderId;
    private LocalDateTime orderDate;
    private OrderState orderState;
    private int deliveryFee;
    private Long paymentId;
    private Long customerNo;
    private String jSessionId;
    private String receiverName;
    private String receiverPhoneNumber;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String req;
    private List<OrderDetailDto> orderDetailDtoList;
}
