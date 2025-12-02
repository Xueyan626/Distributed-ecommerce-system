package storeApplication.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestMessage {
    private Integer orderId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private String transactionId;
}
