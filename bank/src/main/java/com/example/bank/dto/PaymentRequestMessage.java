package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestMessage {
    private Integer orderId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
}


