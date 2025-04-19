package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.BookRepository;
import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingRepository;
import com.ardnaxela.library_management_system.Mapper.BookMapper;
import com.ardnaxela.library_management_system.Notification.Notification;
import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Notification.NotificationRepository;
import com.ardnaxela.library_management_system.Services.NotificationService;
import com.ardnaxela.library_management_system.Services.WebNotificationConsumerServices;
import com.ardnaxela.library_management_system.User.UserRepository;
import com.ardnaxela.library_management_system.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebNotificationConsumerImpl implements WebNotificationConsumerServices {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;

    @Override
    @RabbitListener(queues = RabbitMQConfig.WEB_QUEUE)
    public void consumeWebNotification(NotificationEvent event) {
        if ("NEW_BOOK_ARRIVAL".equals(event.getType())) {
            log.info("New book arrival notification: {}", event);
            return;
        }

        Borrowing borrowing = borrowingRepository.findById(event.getBorrowingId())
                .orElseThrow(() -> new RuntimeException("Borrowing not found"));

        Notification notification = new Notification();
        notification.setMessage(event.getMessage());
        notification.setNotificationType(event.getType());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        if (borrowing.getMember() != null) {
            notification.setUser(borrowing.getMember().getUser());
        }
        if (borrowing.getBook() != null) {
            notification.setBook(borrowing.getBook());
        }

        notificationRepository.save(notification);
        log.info("Received notification from RabbitMQ: {}", event);
    }
}
