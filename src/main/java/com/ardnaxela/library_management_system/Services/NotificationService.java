package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.User.User;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> getUserNotifications();

    void addEmitter(SseEmitter emitter);
    void removeEmitter(SseEmitter emitter);
    void sendNotification(NotificationDTO notification);

    void markAllAsRead();

    User getCurrentUser();

//    NotificationDTO createSystemNotification(Long userId, Long bookId,
//                                             String message, NotificationEvent.NotificationType type);

    void markAsRead(Long id);

    NotificationDTO updateNotification(NotificationDTO notificationDTO );

    String createNotification(NotificationDTO notificationDTO);

    void deleteNotification(Long id);

    List<NotificationDTO> getNotificationsByType(String newBookArrival);
}
