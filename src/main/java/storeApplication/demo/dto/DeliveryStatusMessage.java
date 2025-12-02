package storeApplication.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusMessage {
    private Integer orderId;
    private String status; // received, picked_up, in_transit, delivered, lost, cancelled
    private String message;
    private Long timestamp;
    private String trackingId;
}



