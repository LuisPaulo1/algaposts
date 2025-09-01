package com.algaposts.post.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TEXT_PROCESS = "text-processor-service.post-processing.v1.q";
    public static final String QUEUE_TEXT_PROCESS = TEXT_PROCESS + ".q";
    public static final String QUEUE_POST_SERVICE = "post-service.post-processing-result.v1.q";
    public static final String EXCHANGE_POST_PROCESS = "post-processing-exchange.v1.e";
    public static final String DLQ_TEXT_PROCESS = TEXT_PROCESS + ".dlq";
    public static final String DLX_POST_PROCESS = "post-processing-dlx";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public DirectExchange exchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_POST_PROCESS).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX_POST_PROCESS).durable(true).build();
    }

    @Bean
    public Queue queueTextProcessor() {
        return QueueBuilder.durable(QUEUE_TEXT_PROCESS)
                .withArgument("x-dead-letter-exchange", DLX_POST_PROCESS)
                .withArgument("x-dead-letter-routing-key", "dlq")
                .build();
    }

    @Bean
    public Queue queuePostService() {
        return QueueBuilder.durable(QUEUE_POST_SERVICE)
                .withArgument("x-dead-letter-exchange", DLX_POST_PROCESS)
                .withArgument("x-dead-letter-routing-key", "dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_TEXT_PROCESS).build();
    }

    @Bean
    public Binding bindingTextProcessor() {
        return BindingBuilder.bind(queueTextProcessor()).to(exchange()).with("post.created");
    }

    @Bean
    public Binding bindingPostService() {
        return BindingBuilder.bind(queuePostService()).to(exchange()).with("post.resulted");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("dlq");
    }
}
