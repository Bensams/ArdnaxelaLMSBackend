package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Notification.NotificationEvent;

public interface SmsNotificationConsumerServices {

    void consumeSmsNotification(NotificationEvent event);
}
