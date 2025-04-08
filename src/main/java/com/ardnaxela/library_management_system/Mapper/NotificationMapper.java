package com.ardnaxela.library_management_system.Mapper;

import com.ardnaxela.library_management_system.Notification.Notification;
import com.ardnaxela.library_management_system.Notification.NotificationDTO;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setBook(notification.getBook());
        notificationDTO.setUser(notification.getUser());
        notificationDTO.setNotificationType(notification.getNotificationType());
        notificationDTO.setMessage(notification.getMessage());

        return notificationDTO;

    }


}
