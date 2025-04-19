package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Notification.NotificationEvent;

public interface WebNotificationConsumerServices {

    void consumeWebNotification(NotificationEvent event);
}
