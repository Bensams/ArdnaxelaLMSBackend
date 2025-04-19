package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.NotificationPublisher;
import com.ardnaxela.library_management_system.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationPublisherImpl implements NotificationPublisher {

    private final AmqpTemplate rabbitTemplate;

    @Override
    public void publishNotification(NotificationEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, "notification.web", event);
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, "notification.email", event);
        } catch (Exception e) {
            // Implement retry logic or dead letter queue
            System.err.println("Failed to publish notification: " + e.getMessage());
        }

    }
}
