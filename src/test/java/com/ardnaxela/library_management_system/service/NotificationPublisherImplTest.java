package com.ardnaxela.library_management_system.service;

import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.Impl.NotificationPublisherImpl;
import com.ardnaxela.library_management_system.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherImplTest {

    @Mock
    private AmqpTemplate rabbitTemplate;

    @InjectMocks
    private NotificationPublisherImpl notificationPublisher;

    @Test
    void publishNotification_ShouldSendToWebAndEmailQueues() {
        // Given
        NotificationEvent event = new NotificationEvent();
        event.setMessage("Test message");
        event.setType("TEST_TYPE");
        event.setEmailPreferred(true);
        event.setSmsPreferred(false);

        // When
        notificationPublisher.publishNotification(event);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.NOTIFICATION_EXCHANGE),
                eq("notification.web"),
                eq(event)
        );

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.NOTIFICATION_EXCHANGE),
                eq("notification.email"),
                eq(event)
        );
    }

    @Test
    void testJsonConversion() {
        NotificationEvent event = new NotificationEvent();
        // set fields
        event.setMessage("Test message");
        event.setType("TEST_EMAIL");

        MessageConverter converter = new Jackson2JsonMessageConverter();
        Message message = converter.toMessage(event, new MessageProperties());

        assertNotNull(message.getBody());
        System.out.println(new String(message.getBody()));
    }
}