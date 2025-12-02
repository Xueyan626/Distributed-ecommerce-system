package com.example.deliveryco.repository;

import com.example.deliveryco.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Integer orderId);
    Optional<Delivery> findByTrackingId(String trackingId);
}



