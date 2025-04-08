package com.ardnaxela.library_management_system.Notification;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String notificationType;

    private Book book;
    private User user;

    private String message;
}
