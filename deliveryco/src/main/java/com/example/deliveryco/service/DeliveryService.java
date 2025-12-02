package com.example.deliveryco.service;

import com.example.deliveryco.config.RabbitMQConfig;
import com.example.deliveryco.dto.DeliveryRequestMessage;
import com.example.deliveryco.dto.DeliveryStatusMessage;
import com.example.deliveryco.dto.EmailRequestMessage;
import com.example.deliveryco.model.Delivery;
import com.example.deliveryco.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void processDeliveryRequest(DeliveryRequestMessage request) {
        String timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        log.info("[{}] ========== Delivery Request Received ==========", timeStr);
        log.info("[{}] Order ID: {}", timeStr, request.getOrderId());
        log.info("[{}] Item: {} (Quantity: {})", timeStr, request.getItemName(), request.getQuantity());
        log.info("[{}] Customer: {} ({})", timeStr, request.getCustomerName(), request.getCustomerEmail());
        log.info("[{}] Delivery Address: {}", timeStr, request.getDeliveryAddress());

        // Create delivery record
        Delivery delivery = new Delivery();
        delivery.setOrderId(request.getOrderId());
        delivery.setTrackingId("TRK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        delivery.setStatus("received");
        delivery.setDeliveryAddress(request.getDeliveryAddress());
        delivery.setCustomerEmail(request.getCustomerEmail());
        delivery.setCustomerName(request.getCustomerName());
        delivery.setItemName(request.getItemName());
        delivery.setQuantity(request.getQuantity());

        deliveryRepository.save(delivery);

        log.info("[{}]  Delivery record created - Tracking ID: {}", timeStr, delivery.getTrackingId());
        log.info("[{}]  Status: RECEIVED - Package received at warehouse", timeStr);
        
        // Start delivery process asynchronously
        CompletableFuture.runAsync(() -> processDeliveryFlow(delivery));

        // Send initial status
        sendDeliveryStatus(delivery, "received", "Package received at warehouse");
        
        log.info("[{}]  Delivery process started asynchronously", timeStr);
        log.info("[{}] =================================================", timeStr);
    }

    private void processDeliveryFlow(Delivery delivery) {
        String timeStr;
        try {
            // Step 1: Picked up (after ~10s delay)
            timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            log.info("[{}]  Step 1/3: Waiting 10 seconds before pickup...", timeStr);
            if (isCancelledOrLost(delivery)) return;

            timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            delivery.setStatus("picked_up");
            deliveryRepository.save(delivery);
            sendDeliveryStatus(delivery, "picked_up", "Package picked up by delivery driver");
            sendEmailNotification(delivery, "picked_up");
            log.info("[{}]  Step 1/3: PICKED_UP - Package picked up by driver (Order: {}, Tracking: {})",
                    timeStr, delivery.getOrderId(), delivery.getTrackingId());

            // Step 2: In transit (after ~10s delay)
            timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            log.info("[{}]  Step 2/3: Waiting 10 seconds before transit...", timeStr);
            delay(5000);
            
            if (isCancelledOrLost(delivery)) return;

            timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            delivery.setStatus("in_transit");
            deliveryRepository.save(delivery);
            sendDeliveryStatus(delivery, "in_transit", "Package is in transit to destination");
            log.info("[{}]  Step 2/3: IN_TRANSIT - Package is in transit (Order: {}, Tracking: {})",
                    timeStr, delivery.getOrderId(), delivery.getTrackingId());

            // Step 3: Delivered (after ~10s delay)
            timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            log.info("[{}]  Step 3/3: Waiting 10 seconds before delivery...", timeStr);
            delay(5000);
            
            if (isCancelledOrLost(delivery)) return;
            timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            delivery.setStatus("delivered");
            deliveryRepository.save(delivery);
            sendDeliveryStatus(delivery, "delivered", "Package delivered successfully");
            sendEmailNotification(delivery, "delivered");
            log.info("[{}]  Step 3/3: DELIVERED - Package delivered successfully! (Order: {}, Tracking: {})",
                    timeStr, delivery.getOrderId(), delivery.getTrackingId());
            log.info("[{}]  Delivery completed successfully for order: {}", timeStr, delivery.getOrderId());

        } catch (Exception e) {
            timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            log.error("[{}]  Error processing delivery flow for order {}: {}",
                    timeStr, delivery.getOrderId(), e.getMessage(), e);
            delivery.setStatus("lost");
            deliveryRepository.save(delivery);
            sendDeliveryStatus(delivery, "lost", "Package lost during delivery process");
        }
    }

    private boolean isCancelledOrLost(Delivery delivery) {
        // random 5% loss
        if (random.nextDouble() < 0.05) {
            handlePackageLoss(delivery);
            return true;
        }
        // if cancelled by external message
        return "cancelled".equalsIgnoreCase(delivery.getStatus());
    }

    private void handlePackageLoss(Delivery delivery) {
        String timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        log.warn("[{}]   ========== PACKAGE LOST DETECTED ==========", timeStr);
        log.warn("[{}]   Package LOST during delivery for order: {}", timeStr, delivery.getOrderId());
        log.warn("[{}]   Tracking ID: {}", timeStr, delivery.getTrackingId());
        log.warn("[{}]   This triggers automatic order cancellation, refund, and email notification", timeStr);
        
        delivery.setStatus("lost");
        deliveryRepository.save(delivery);
        sendDeliveryStatus(delivery, "lost", "Package lost during delivery");
        sendEmailNotification(delivery, "lost");
        
        log.warn("[{}]   LOST status sent to store application for order: {}", timeStr, delivery.getOrderId());
        log.warn("[{}] =================================================", timeStr);
    }

    private void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Delivery process interrupted", e);
        }
    }

    private void sendDeliveryStatus(Delivery delivery, String status, String message) {
        String timeStr = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        DeliveryStatusMessage statusMessage = new DeliveryStatusMessage(
                delivery.getOrderId(),
                status,
                message,
                System.currentTimeMillis(),
                delivery.getTrackingId()
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.DELIVERY_EXCHANGE,
                    RabbitMQConfig.DELIVERY_STATUS_ROUTING_KEY,
                    statusMessage,
                    msg -> {
                        // Make message persistent for fault tolerance
                        msg.getMessageProperties().setDeliveryMode(
                                org.springframework.amqp.core.MessageDeliveryMode.PERSISTENT
                        );
                        return msg;
                    }
            );
            log.info("[{}] Delivery status sent to store application - Order: {}, Status: {}, Tracking: {}",
                    timeStr, delivery.getOrderId(), status.toUpperCase(), delivery.getTrackingId());
        } catch (Exception e) {
            log.error("[{}] Failed to send delivery status for order {}: {}",
                    timeStr, delivery.getOrderId(), e.getMessage(), e);
            // Retry logic could be added here for fault tolerance
        }
    }

    private void sendEmailNotification(Delivery delivery, String status) {
        String subject;
        String body;

        switch (status) {
            case "picked_up":
                subject = "Your package has been picked up";
                body = String.format("Dear %s,\n\nYour order #%d (%s) has been picked up and is on its way!\n\nTracking ID: %s\n\nBest regards,\nDeliveryCo Team",
                        delivery.getCustomerName(), delivery.getOrderId(), delivery.getItemName(), delivery.getTrackingId());
                break;
            case "delivered":
                subject = "Your package has been delivered";
                body = String.format("Dear %s,\n\nYour order #%d (%s) has been successfully delivered!\n\nTracking ID: %s\n\nThank you for choosing our service!\n\nBest regards,\nDeliveryCo Team",
                        delivery.getCustomerName(), delivery.getOrderId(), delivery.getItemName(), delivery.getTrackingId());
                break;
            case "lost":
                subject = "Important: Package delivery issue";
                body = String.format("Dear %s,\n\nWe regret to inform you that your order #%d (%s) has been lost during delivery.\n\nTracking ID: %s\n\nPlease contact our customer service for assistance.\n\nBest regards,\nDeliveryCo Team",
                        delivery.getCustomerName(), delivery.getOrderId(), delivery.getItemName(), delivery.getTrackingId());
                break;
            default:
                return; // Don't send email for other statuses
        }

        EmailRequestMessage emailMessage = new EmailRequestMessage(
                delivery.getCustomerEmail(),
                subject,
                body,
                delivery.getOrderId().toString(),
                status
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_REQUEST_ROUTING_KEY,
                    emailMessage
            );
            log.info("Email notification sent for order {}: {}",
                    delivery.getOrderId(), status);
        } catch (Exception e) {
            log.error("Failed to send email notification for order {}: {}",
                    delivery.getOrderId(), e.getMessage());
        }
    }
} 

