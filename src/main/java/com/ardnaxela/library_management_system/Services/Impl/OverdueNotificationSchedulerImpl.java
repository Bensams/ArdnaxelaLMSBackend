package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingRepository;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.NotificationPublisher;
import com.ardnaxela.library_management_system.Services.OverdueNotificationSchedulerServices;
import com.ardnaxela.library_management_system.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OverdueNotificationSchedulerImpl implements OverdueNotificationSchedulerServices {

    private final BorrowingRepository borrowingRepository;
    private final NotificationPublisher notificationPublisher;

    // Due date reminder (3 days before)
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAllBookDueNotifications() {
        checkDueDateReminders();  // 3-day reminder
        checkOneDayReminder();    // 1-day reminder
        checkOverdueBooks();      // Overdue books
    }

    private void checkOneDayReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Borrowing> borrowings = borrowingRepository
                .findByStatusAndDueDateBetween(
                        tomorrow.withHour(0).withMinute(0),
                        tomorrow.withHour(23).withMinute(59));

        borrowings.forEach(borrowing -> {
            NotificationEvent event = createReminderEvent(borrowing, 1);
            event.setType("DUE_DATE_REMINDER_1DAY");
            notificationPublisher.publishNotification(event);
        });
    }

    private NotificationEvent createReminderEvent(Borrowing borrowing, int daysBefore) {
        NotificationEvent event = new NotificationEvent();
        // ... common setup code ...
        event.setMessage(String.format(
                "Reminder: The book '%s' is due in %d day%s. (Due Date: %s)",
                borrowing.getBook().getTitle(),
                daysBefore,
                daysBefore > 1 ? "s" : "",
                borrowing.getDueDate().toLocalDate()
        ));
        return event;
    }

    public void checkDueDateReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);

        // Find books due in exactly 3 days
        List<Borrowing> upcomingDueBorrowings = borrowingRepository
                .findByStatusAndDueDateBetween(threeDaysLater.withHour(0).withMinute(0),
                        now.plusDays(3).withHour(23).withMinute(59));

        upcomingDueBorrowings.forEach(borrowing -> {
            NotificationEvent event = new NotificationEvent();
            event.setBorrowingId(borrowing.getId());

            if (borrowing.getMember() != null) {
                User user = borrowing.getMember().getUser();
                event.setEmailPreferred(user.isEmailNotificationsEnabled());
                event.setSmsPreferred(user.isSmsNotificationsEnabled());
            } else {
                event.setEmailPreferred(borrowing.getGuestEmail() != null);
                event.setSmsPreferred(borrowing.getGuestPhoneNumber() != null);
            }

            event.setType("DUE_DATE_REMINDER");
            event.setMessage("Reminder: The book '" + borrowing.getBook().getTitle() +
                    "' is due in 3 days (Due Date: " + borrowing.getDueDate().toLocalDate() + "). " +
                    "Please return or renew it before the due date.");

            notificationPublisher.publishNotification(event);
        });
    }

    // Automation
    @Override
    public void checkOverdueBooks() {
        LocalDateTime now = LocalDateTime.now();
        List<Borrowing> overdueBorrowings = borrowingRepository
                .findByStatusAndDueDateBefore("BORROWED", now);

        overdueBorrowings.forEach(borrowing -> {
            NotificationEvent event = new NotificationEvent();

            event.setBorrowingId(borrowing.getId());

            if (borrowing.getMember() != null) {
                User user = borrowing.getMember().getUser();
                event.setEmailPreferred(user.isEmailNotificationsEnabled());
                event.setSmsPreferred(user.isSmsNotificationsEnabled());
            } else {
                event.setEmailPreferred(borrowing.getGuestEmail() != null);
                event.setSmsPreferred(borrowing.getGuestPhoneNumber() != null);
            }

            event.setType("OVERDUE_ALERT");
            event.setMessage("The book " + borrowing.getBook().getTitle() +
                    " is overdue. Please return it to the library!.");

            borrowing.setStatus("OVERDUE");
            borrowingRepository.save(borrowing);
            notificationPublisher.publishNotification(event);
        });
    }
}
