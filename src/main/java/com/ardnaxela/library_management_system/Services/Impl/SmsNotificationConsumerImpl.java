package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.SmsNotificationConsumerServices;
import com.ardnaxela.library_management_system.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsNotificationConsumerImpl implements SmsNotificationConsumerServices {
//    TODO: Implement SMS sending logic using a service like Twilio or Nexmo
    @Override
    @RabbitListener(queues = RabbitMQConfig.SMS_QUEUE)
    public void consumeSmsNotification(NotificationEvent event) {
        if (event.isSmsPreferred()) {
//            try {
//                String phoneNumber = getUserPhoneNumber(event.getUserId()); // Implement this
//                String message = event.getMessage();
//
//                // Example using Twilio (free tier available)
//                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//                Message.creator(
//                        new PhoneNumber(phoneNumber),
//                        new PhoneNumber("+1234567890"), // Your Twilio number
//                        message
//                ).create();
//            } catch (Exception e) {
//                // Implement retry logic
//                System.err.println("Failed to send SMS: " + e.getMessage());
//            }
        }
    }
}
