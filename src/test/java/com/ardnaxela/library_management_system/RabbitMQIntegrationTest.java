package com.ardnaxela.library_management_system;

import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class RabbitMQIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void whenNotificationSent_thenEmailConsumerReceivesIt() {
        // Given
        NotificationEvent event = new NotificationEvent();
        event.setMessage("Test message");
        event.setType("TEST_TYPE");
        event.setEmailPreferred(true);

        // When
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                "notification.email",
                event
        );

        // Then
        verify(mailSender, timeout(5000)).send(any(SimpleMailMessage.class));
    }
}