package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseMessage {
    private Integer orderId;
    private String status; // SUCCESS, FAILED
    private String transactionId;
    private String message;
}


