package com.example.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.example.bank.dto.PaymentResponseMessage;
import com.example.bank.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    public void sendPaymentResponse(Integer orderId, String status, String transactionId, String message) {
        PaymentResponseMessage response = new PaymentResponseMessage(
            orderId,
            status,
            transactionId,
            message
        );
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.BANK_EXCHANGE,
            RabbitMQConfig.PAYMENT_RESPONSE_ROUTING_KEY,
            response
        );
    }
}

