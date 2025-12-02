package com.example.deliveryco.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String DELIVERY_EXCHANGE = "delivery.exchange";
    public static final String EMAIL_EXCHANGE = "email.exchange";

    // Queue names
    public static final String DELIVERY_REQUEST_QUEUE = "delivery.request.queue";
    public static final String DELIVERY_STATUS_QUEUE = "delivery.status.queue";
    public static final String EMAIL_REQUEST_QUEUE = "email.request.queue";

    // Routing keys
    public static final String DELIVERY_REQUEST_ROUTING_KEY = "delivery.request";
    public static final String DELIVERY_STATUS_ROUTING_KEY = "delivery.status";
    public static final String EMAIL_REQUEST_ROUTING_KEY = "email.request";

    // Delivery Exchange and Queues
    @Bean
    public TopicExchange deliveryExchange() {
        return new TopicExchange(DELIVERY_EXCHANGE);
    }

    @Bean
    public Queue deliveryRequestQueue() {
        return QueueBuilder.durable(DELIVERY_REQUEST_QUEUE).build();
    }

    @Bean
    public Queue deliveryStatusQueue() {
        return QueueBuilder.durable(DELIVERY_STATUS_QUEUE).build();
    }

    @Bean
    public Binding deliveryRequestBinding() {
        return BindingBuilder
                .bind(deliveryRequestQueue())
                .to(deliveryExchange())
                .with(DELIVERY_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding deliveryStatusBinding() {
        return BindingBuilder
                .bind(deliveryStatusQueue())
                .to(deliveryExchange())
                .with(DELIVERY_STATUS_ROUTING_KEY);
    }

    // Email Exchange and Queues
    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Queue emailRequestQueue() {
        return QueueBuilder.durable(EMAIL_REQUEST_QUEUE).build();
    }

    @Bean
    public Binding emailRequestBinding() {
        return BindingBuilder
                .bind(emailRequestQueue())
                .to(emailExchange())
                .with(EMAIL_REQUEST_ROUTING_KEY);
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
