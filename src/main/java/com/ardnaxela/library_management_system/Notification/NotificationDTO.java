package com.ardnaxela.library_management_system.Notification;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String notificationType;

    private Long bookID;
    private Long userID;
    private Book book;

    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

}
