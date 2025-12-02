package com.example.deliveryco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestMessage {
    private Integer orderId;
    private String deliveryAddress;
    private String customerEmail;
    private String customerName;
    private String itemName;
    private Integer quantity;
    private Long timestamp;
}
