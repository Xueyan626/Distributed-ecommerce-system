package com.example.bank.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.example.bank.dto.PaymentRequestMessage;
import com.example.bank.service.PaymentService;

@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = "payment.request.queue")
    public void handlePaymentRequest(PaymentRequestMessage request) {
        try {
            paymentService.processPayment(request);
        } catch (Exception e) {
            paymentService.sendPaymentResponse(
                request.getOrderId(), 
                "FAILED", 
                null, 
                "Payment processing failed: " + e.getMessage()
            );
        }
    }
}
