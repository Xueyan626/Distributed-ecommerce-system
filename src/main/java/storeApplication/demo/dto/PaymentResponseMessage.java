package storeApplication.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseMessage {
    private Integer orderId;
    private String status; // SUCCESS, FAILED, PENDING
    private String transactionId;
    private String message;
}


