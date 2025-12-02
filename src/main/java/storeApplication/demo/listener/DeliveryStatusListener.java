package storeApplication.demo.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import storeApplication.demo.dto.DeliveryStatusMessage;
import storeApplication.demo.service.OrderService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusListener {

    private final OrderService orderService;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @RabbitListener(queues = "delivery.status.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleDeliveryStatus(DeliveryStatusMessage message) {
        String currentTime = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String messageTime = message.getTimestamp() != null ?
                LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getTimestamp()), 
                        ZoneId.systemDefault()).format(TIMESTAMP_FORMATTER) : "N/A";
        
        log.info("[{}] ========== Delivery Status Update ==========", currentTime);
        log.info("[{}] Order ID: {}", currentTime, message.getOrderId());
        log.info("[{}] Status: {}", currentTime, message.getStatus().toUpperCase());
        log.info("[{}] Tracking ID: {}", currentTime, message.getTrackingId());
        log.info("[{}] Message: {}", currentTime, message.getMessage());
        log.info("[{}] Message Timestamp: {}", currentTime, messageTime);
        
        try {
            switch (message.getStatus().toUpperCase()) {
                case "DELIVERED":
                    log.info("[{}] Package delivered successfully for order: {}", 
                            currentTime, message.getOrderId());
                    orderService.handleDeliverySuccess(message.getOrderId());
                    break;
                    
                case "LOST":
                    log.warn("[{}] Package LOST for order: {}", 
                            currentTime, message.getOrderId());
                    log.warn("[{}] Triggering automatic order cancellation and refund...", currentTime);
                    orderService.handleDeliveryLost(message.getOrderId());
                    break;
                    
                case "RECEIVED":
                    log.info("[{}] Package received at warehouse for order: {}", 
                            currentTime, message.getOrderId());
                    break;
                    
                case "PICKED_UP":
                    log.info("[{}] Package picked up by driver for order: {}", 
                            currentTime, message.getOrderId());
                    break;
                    
                case "IN_TRANSIT":
                    log.info("[{}] Package in transit for order: {}", 
                            currentTime, message.getOrderId());
                    break;
                    
                case "CANCELLED":
                    log.info("[{}] Delivery cancelled for order: {}", 
                            currentTime, message.getOrderId());
                    break;
                    
                default:
                    log.info("[{}] Delivery status update: {} for order: {}", 
                            currentTime, message.getStatus(), message.getOrderId());
            }
            
            log.info("[{}] =================================================", currentTime);
        } catch (Exception e) {
            log.error("[{}] Error processing delivery status for order {}: {}", 
                    currentTime, message.getOrderId(), e.getMessage(), e);
        }
    }
}



