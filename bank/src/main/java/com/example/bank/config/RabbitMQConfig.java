package com.example.bank.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String BANK_EXCHANGE = "bank.exchange";

    // Queue names
    public static final String PAYMENT_REQUEST_QUEUE = "payment.request.queue";
    public static final String PAYMENT_RESPONSE_QUEUE = "payment.response.queue";
    public static final String REFUND_REQUEST_QUEUE = "refund.request.queue";

    // Routing keys
    public static final String PAYMENT_REQUEST_ROUTING_KEY = "payment.request";
    public static final String PAYMENT_RESPONSE_ROUTING_KEY = "payment.response";
    public static final String REFUND_REQUEST_ROUTING_KEY = "refund.request";

    // Bank Exchange
    @Bean
    public TopicExchange bankExchange() {
        return new TopicExchange(BANK_EXCHANGE);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(PAYMENT_REQUEST_QUEUE).build();
    }

    @Bean
    public Queue paymentResponseQueue() {
        return QueueBuilder.durable(PAYMENT_RESPONSE_QUEUE).build();
    }

    @Bean
    public Queue refundRequestQueue() {
        return QueueBuilder.durable(REFUND_REQUEST_QUEUE).build();
    }

    @Bean
    public Binding paymentRequestBinding() {
        return BindingBuilder
                .bind(paymentRequestQueue())
                .to(bankExchange())
                .with(PAYMENT_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding paymentResponseBinding() {
        return BindingBuilder
                .bind(paymentResponseQueue())
                .to(bankExchange())
                .with(PAYMENT_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Binding refundRequestBinding() {
        return BindingBuilder
                .bind(refundRequestQueue())
                .to(bankExchange())
                .with(REFUND_REQUEST_ROUTING_KEY);
    }

    // Message Converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

