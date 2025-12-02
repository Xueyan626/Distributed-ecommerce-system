package com.example.deliveryco.listener;

import com.example.deliveryco.dto.DeliveryRequestMessage;
import com.example.deliveryco.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryRequestListener {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = "delivery.request.queue")
    public void handleDeliveryRequest(DeliveryRequestMessage message) {
        String timeStr = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        log.info("[{}] Received delivery request from store application", timeStr);
        log.info("[{}] Order ID: {}", timeStr, message.getOrderId());
        
        try {
            deliveryService.processDeliveryRequest(message);
        } catch (Exception e) {
            log.error("[{}] Error processing delivery request for order {}: {}", 
                    timeStr, message.getOrderId(), e.getMessage(), e);
            // Message will be requeued if RabbitMQ is configured with retry policy
            // This demonstrates fault tolerance - message persists even if processing fails
        }
    }
}

