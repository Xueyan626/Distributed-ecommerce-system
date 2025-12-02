package com.example.deliveryco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestMessage {
    private String toAddress;
    private String subject;
    private String body;
    private String orderId;
    private String status;
}
