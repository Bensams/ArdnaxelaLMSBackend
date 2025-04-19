package com.ardnaxela.library_management_system.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String SMS_QUEUE = "sms.queue";
    public static final String WEB_QUEUE = "web.queue";

    // Exchange and Queue declarations remain the same
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQErrorConfig.DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQErrorConfig.DLQ_QUEUE)
                .build();
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(SMS_QUEUE, true);
    }

    @Bean
    public Queue webQueue() {
        return QueueBuilder.durable(WEB_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQErrorConfig.DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RabbitMQErrorConfig.DLQ_QUEUE)
                .build();
    }

    // Binding declarations remain the same
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange exchange) {
        return BindingBuilder.bind(emailQueue)
                .to(exchange)
                .with("notification.email");
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(smsQueue)
                .to(exchange)
                .with("notification.sms");
    }

    @Bean
    public Binding webBinding(Queue webQueue, TopicExchange exchange) {
        return BindingBuilder.bind(webQueue)
                .to(exchange)
                .with("notification.web");
    }

    // Single message converter bean
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Single RabbitTemplate bean
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Listener container factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor retryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryInterceptor); // Add retry interceptor here
        return factory;
    }
}
