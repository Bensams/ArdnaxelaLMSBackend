package com.ardnaxela.library_management_system.Controller;

import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.Services.NotificationService;
import com.ardnaxela.library_management_system.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications() {
        // Get current user ID from security context
        List<NotificationDTO> notifications = notificationService.getUserNotifications();
        return ResponseEntity.ok(notifications);
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
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
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