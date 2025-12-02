package com.example.deliveryco.controller;

import com.example.deliveryco.model.Delivery;
import com.example.deliveryco.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;

    @GetMapping("/status/{orderId}")
    public ResponseEntity<Delivery> getDeliveryStatus(@PathVariable Integer orderId) {
        Optional<Delivery> delivery = deliveryRepository.findByOrderId(orderId);
        return delivery.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{trackingId}")
    public ResponseEntity<Delivery> getDeliveryByTrackingId(@PathVariable String trackingId) {
        Optional<Delivery> delivery = deliveryRepository.findByTrackingId(trackingId);
        return delivery.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAll();
        return ResponseEntity.ok(deliveries);
    }
}

