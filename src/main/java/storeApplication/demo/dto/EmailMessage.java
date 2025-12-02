package storeApplication.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String orderId;  // Changed from Integer to String to match email service
    private String toAddress;
    private String subject;  // Added to match email service
    private String body;
    private String status;   // Added to match email service
}
