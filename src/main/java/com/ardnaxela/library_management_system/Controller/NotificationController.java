package com.ardnaxela.library_management_system.Controller;

import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.EmailNotificationConsumerServices;
import com.ardnaxela.library_management_system.Services.NotificationPublisher;
import com.ardnaxela.library_management_system.Services.NotificationService;
import com.ardnaxela.library_management_system.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final EmailNotificationConsumerServices emailNotificationConsumerServices;
    private final NotificationPublisher notificationPublisher;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications() {
        // Get current user ID from security context
        List<NotificationDTO> notifications = notificationService.getUserNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("new-arrivals")
    public List<NotificationDTO> getNewArrivals() {
        // Get current user ID from security context
        List<NotificationDTO> notifications = notificationService.getNotificationsByType("NEW_BOOK_ARRIVAL");
        return notifications;
    }

    @PutMapping("/send-email")
    public String sendTestEmail(@RequestBody NotificationEvent event) {
        emailNotificationConsumerServices.consumeEmailNotification(event);
        return "Email sent successfully";
    }

    @PutMapping("/publish")
    public String publishNotification(@RequestBody NotificationEvent event) {
        notificationPublisher.publishNotification(event);
        return "Publish Successfully!";
    }

    @GetMapping("/stream")
    @CrossOrigin
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter(3600000L); // 1 hour timeout

        // Store emitter for later use
        notificationService.addEmitter(emitter);

        emitter.onCompletion(() -> notificationService.removeEmitter(emitter));
        emitter.onTimeout(() -> notificationService.removeEmitter(emitter));

        return emitter;
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<NotificationDTO> updateNotification(@RequestBody NotificationDTO notificationDTO) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        NotificationDTO updatedNotification = notificationService.updateNotification(notificationDTO);
        return ResponseEntity.ok(updatedNotification);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<String> createNotification(@RequestBody NotificationDTO notificationDTO) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }

        // 1. Save to database
        String createdNotification = notificationService.createNotification(notificationDTO);
        // 2. Publish to rabbitMQ

        return ResponseEntity.status(201).body(createdNotification);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}