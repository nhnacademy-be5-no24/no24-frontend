package com.nhnacademy.frontend.main.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Builder
public class OrdersResponseDto {
    public enum OrderState {
        COMPLETE_PAYMENT, WAITING, SHIPPING, COMPLETED, RETURNS, ORDER_CANCELED, PURCHASE_CONFIRMED
    }

    private String orderId;
    private String bookTitle;
    private Long bookSalePrice;
    private String wrapName;
    private Long wrapCost;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate orderDate;
    private String receiverName;
    private String receiverPhoneNumber;
    private String address;
    private String addressDetail;
    private OrderState orderState;

    public OrdersResponseDto(String orderId,
                             String bookTitle,
                             Long bookSalePrice,
                             String wrapName,
                             Long wrapCost,
                             LocalDate orderDate,
                             String receiverName,
                             String receiverPhoneNumber,
                             String address,
                             String addressDetail,
                             OrderState orderState) {
        this.orderId = orderId;
        this.bookTitle = bookTitle;
        this.bookSalePrice = bookSalePrice;
        this.wrapName = wrapName;
        this.wrapCost = wrapCost;
        this.orderDate = orderDate;
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.address = address;
        this.addressDetail = addressDetail;
        this.orderState = orderState;
    }
}
