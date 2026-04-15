package com.elib.borrowing.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "elib.notifications";
    public static final String LOAN_CREATED_KEY = "email.loan.created";
    public static final String LOAN_RETURNED_KEY = "email.loan.returned";
    public static final String LOAN_OVERDUE_KEY = "email.loan.overdue";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
