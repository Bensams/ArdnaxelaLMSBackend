package com.ardnaxela.library_management_system.Mapper;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Notification.Notification;
import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.User.User;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setBookID(notification.getBook().getId());
        notificationDTO.setUserID(notification.getUser().getId());
        notificationDTO.setNotificationType(notification.getNotificationType());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setRead(notification.isRead());
        notificationDTO.setCreatedAt(notification.getCreatedAt());
        notificationDTO.setBook(notification.getBook());

        return notificationDTO;

    }

    public static Notification toEntity(NotificationDTO notificationDTO) {
        if (notificationDTO == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setId(notificationDTO.getId());
        if (notificationDTO.getBookID() != null) {
                Book book = new Book();
                book.setId(notificationDTO.getBookID());
                notification.setBook(book);
            }
        if (notificationDTO.getUserID() != null) {
            User user = new User();
            user.setId(notificationDTO.getUserID());
            notification.setUser(user);
        }
        notification.setNotificationType(notificationDTO.getNotificationType());
        notification.setMessage(notificationDTO.getMessage());
        notification.setRead(notificationDTO.isRead());
        notification.setBook(notificationDTO.getBook());

        return notification;
    }


}
