package com.example.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.email.dto.EmailMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    public void sendEmail(EmailMessage message) {
        System.out.println("=== EMAIL SENT ===");
        System.out.println("Order ID: " + message.getOrderId());
        System.out.println("To: " + message.getToAddress());
        if (message.getSubject() != null) {
            System.out.println("Subject: " + message.getSubject());
        }
        System.out.println("Message: " + message.getBody());
        if (message.getStatus() != null) {
            System.out.println("Status: " + message.getStatus());
        }
        System.out.println("==================");
    }
}
