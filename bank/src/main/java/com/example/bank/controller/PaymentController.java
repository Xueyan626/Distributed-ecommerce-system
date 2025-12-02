package com.example.bank.controller;

import com.example.bank.dto.PaymentRequestMessage;
import com.example.bank.dto.RefundRequestMessage;
import com.example.bank.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Process payment via REST and return immediate result
    @PostMapping("/payments")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody PaymentRequestMessage request) {
        PaymentService.PaymentResult result = paymentService.processPayment(request);
        return ResponseEntity.ok(Map.of(
                "status", result.getStatus(),
                "orderId", request.getOrderId(),
                "transactionId", result.getTransactionId() != null ? result.getTransactionId() : "",
                "message", result.getMessage()
        ));
    }

    // Trigger refund processing via REST; result will still be published via MQ
    @PostMapping("/refunds")
    public ResponseEntity<Map<String, Object>> createRefund(@RequestBody RefundRequestMessage request) {
        paymentService.processRefund(request);
        return ResponseEntity.accepted().body(Map.of(
                "status", "ACCEPTED",
                "orderId", request.getOrderId()
        ));
    }
}


