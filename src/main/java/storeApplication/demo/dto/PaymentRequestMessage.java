package storeApplication.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestMessage {
    private Integer orderId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private String requestId;
}


