package com.example.email.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String orderId;  // Changed from Integer to String to match deliveryco's EmailRequestMessage
    private String toAddress;
    private String subject;  // Added to match deliveryco's EmailRequestMessage
    private String body;
    private String status;   // Added to match deliveryco's EmailRequestMessage
}
