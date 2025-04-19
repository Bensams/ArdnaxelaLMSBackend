package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;

public interface NotificationPublisher {

    void publishNotification(NotificationEvent event);
}
