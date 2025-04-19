package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;

public interface EmailNotificationConsumerServices {
    void consumeEmailNotification(NotificationEvent event);
}
