package com.ardnaxela.library_management_system.Notification;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private Long borrowingId;
    private String type;
    private String message;
    private boolean emailPreferred;
    private boolean smsPreferred;

    // Enum for notification types
//    public enum NotificationType {
//        NEW_BOOK_ARRIVAL,
//        BORROWING_APPROVED,
//        BORROWING_REJECTED,
//        DUE_DATE_REMINDER,
//        OVERDUE_ALERT
//    }

    // Constructors, getters, setters
}