package com.example.bank.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.example.bank.dto.RefundRequestMessage;
import com.example.bank.service.PaymentService;

@Component
@RequiredArgsConstructor
public class RefundRequestListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = "refund.request.queue")
    public void handleRefundRequest(RefundRequestMessage request) {
        try {
            paymentService.processRefund(request);
        } catch (Exception e) {
            paymentService.sendPaymentResponse(
                request.getOrderId(), 
                "FAILED", 
                null, 
                "Refund processing failed: " + e.getMessage()
            );
        }
    }
}
