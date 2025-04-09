package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.User.User;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getUserNotifications();

    void markAllAsRead();

    User getCurrentUser();

    void markAsRead(Long id);

    NotificationDTO updateNotification(NotificationDTO notificationDTO );

    NotificationDTO createNotification(NotificationDTO notificationDTO);

    void deleteNotification(Long id);
}
