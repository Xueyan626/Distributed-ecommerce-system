package storeApplication.demo.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import storeApplication.demo.dto.PaymentResponseMessage;
import storeApplication.demo.service.OrderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResponseListener {

    private final OrderService orderService;

    @RabbitListener(queues = "payment.response.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handlePaymentResponse(PaymentResponseMessage message) {
        log.info("Received payment response for order {}: {}", message.getOrderId(), message.getStatus());
        
        try {
            if ("SUCCESS".equals(message.getStatus())) {
                orderService.handlePaymentSuccess(message.getOrderId());
            } else if ("FAILED".equals(message.getStatus())) {
                orderService.handlePaymentFailure(message.getOrderId());
            } else {
                log.warn("Unknown payment status: {}", message.getStatus());
            }
        } catch (Exception e) {
            log.error("Error processing payment response for order {}: {}", message.getOrderId(), e.getMessage());
        }
    }
}


