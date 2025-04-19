package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.BookRepository;
import com.ardnaxela.library_management_system.Mapper.NotificationMapper;
import com.ardnaxela.library_management_system.Notification.Notification; // Confirm Notification class correctness
//.if true by depend request."+
import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Notification.NotificationRepository;
import com.ardnaxela.library_management_system.Services.NotificationPublisher;
import com.ardnaxela.library_management_system.Services.NotificationService;
import com.ardnaxela.library_management_system.Services.WebNotificationConsumerServices;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository; // Ensure this is correctly imported
    private final WebNotificationConsumerServices webNotificationConsumer;
    private final NotificationPublisher notificationPublisher; // Ensure this is correctly imported
    private final Set<SseEmitter> emitters = Collections.synchronizedSet(new HashSet<>());

    @Override
    public List<NotificationDTO> getUserNotifications() {
        User currentUser = getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }

    @Override
    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    @Override
    public void sendNotification(NotificationDTO notification) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .data(notification)
                        .name("notification"));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });

        deadEmitters.forEach(emitters::remove);
    }


    @Override
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository.findByUserAndReadFalse(currentUser);
        unreadNotifications.forEach(n -> n.setRead(true)); // Ensure Notification class has setRead method
        notificationRepository.saveAll(unreadNotifications);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public NotificationDTO updateNotification(NotificationDTO notificationDTO) {
        Notification notification = notificationRepository.findById(notificationDTO.getId())
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        NotificationDTO updatedNotificationDTO = NotificationMapper.toDTO(notification);
        updatedNotificationDTO.setNotificationType(notificationDTO.getNotificationType());
        updatedNotificationDTO.setMessage(notificationDTO.getMessage());
        updatedNotificationDTO.setRead(notificationDTO.isRead());
        updatedNotificationDTO.setUserID(notificationDTO.getUserID());
        updatedNotificationDTO.setBookID(notificationDTO.getBookID());


        notificationRepository.save(NotificationMapper.toEntity(updatedNotificationDTO));

        return updatedNotificationDTO;
    }

//    @Override
//    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
//        // First create the database notification
//        Notification notification = NotificationMapper.toEntity(notificationDTO);
//        notification.setRead(false);
//        notification.setCreatedAt(LocalDateTime.now());
//        notification.setBook(bookRepository.findById(notificationDTO.getBookID())
//                .orElseThrow(() -> new RuntimeException("Book not found")));
//
//        Notification savedNotification = notificationRepository.save(notification);
//
//        // Then publish the notification event
//        NotificationEvent event = new NotificationEvent();
//        if (notificationDTO.getUserID() != null) {
//            event.setUserId(notificationDTO.getUserID());
//        }
//        event.setBookId(notificationDTO.getBookID());
//        event.setMessage(notificationDTO.getMessage());
//        event.setType(NotificationEvent.NotificationType.valueOf(notificationDTO.getNotificationType()));
//
//
//        // Set delivery preferences (assuming user preferences are stored)
//        if (notificationDTO.getUserID() != null) {
//            User user = userRepository.findById(notificationDTO.getUserID())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//            event.setEmailPreferred(user.isEmailNotificationsEnabled());
//            event.setSmsPreferred(user.isSmsNotificationsEnabled());
//        }
//
//        notificationPublisher.publishNotification(event);
//
//        return NotificationMapper.toDTO(savedNotification);
//    }

    @Override
    public String createNotification(NotificationDTO notificationDTO) {
        if (notificationDTO.getNotificationType() == null) {
            throw new IllegalArgumentException("Notification type is required");
        }

        // First create the database notification
        Notification notification = NotificationMapper.toEntity(notificationDTO);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Set book if available
        if (notificationDTO.getBookID() != null) {
            notification.setBook(bookRepository.findById(notificationDTO.getBookID())
                    .orElseThrow(() -> new RuntimeException("Book not found")));
        }

        Notification savedNotification = notificationRepository.save(notification);

        return "Notification created successfully!";
    }


    @Override
    public void deleteNotification(Long id) {
        if(notificationRepository.findById(id).isPresent()){
            notificationRepository.deleteById(id);
        } else {
            throw new RuntimeException("Notification not found");
        }
    }

    @Override
    public List<NotificationDTO> getNotificationsByType(String notificationType) {
        return notificationRepository.findByNotificationTypeOrderByCreatedAtDesc(notificationType)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
