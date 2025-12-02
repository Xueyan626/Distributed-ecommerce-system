package com.example.email.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.example.email.dto.EmailMessage;
import com.example.email.service.EmailService;
import com.rabbitmq.client.Channel;

@Component
@RequiredArgsConstructor
public class EmailRequestListener {

    private final EmailService emailService;

    @RabbitListener(queues = "email.request.queue", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
    public void handleEmailRequest(EmailMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            emailService.sendEmail(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                channel.basicNack(tag, false, true);
            } catch (Exception nackException) {
                System.err.println("Failed to nack message: " + nackException.getMessage());
            }
        }
    }
}
