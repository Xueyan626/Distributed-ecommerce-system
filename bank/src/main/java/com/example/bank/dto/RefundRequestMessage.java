package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestMessage {
    private Integer orderId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private String transactionId;
}
