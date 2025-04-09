package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.BookRepository;
import com.ardnaxela.library_management_system.Mapper.NotificationMapper;
import com.ardnaxela.library_management_system.Notification.Notification; // Confirm Notification class correctness
//.if true by depend request."+
import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.Notification.NotificationRepository;
import com.ardnaxela.library_management_system.Services.NotificationService;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository; // Ensure this is correctly imported

    @Override
    public List<NotificationDTO> getUserNotifications() {
        User currentUser = getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(notification -> NotificationMapper.toDTO((Notification) notification))
                .collect(Collectors.toList());
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

    @Override
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = NotificationMapper.toEntity(notificationDTO);
        notification.setRead(false); // Default to unread
        notification.setCreatedAt(LocalDateTime.now()); // Set current time
        notification.setBook(bookRepository.findById(notificationDTO.getBookID())
                .orElseThrow(() -> new RuntimeException("Book not found")));
        Notification savedNotification = notificationRepository.save(notification);
        return NotificationMapper.toDTO(savedNotification);
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
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
