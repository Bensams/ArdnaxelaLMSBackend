package com.ardnaxela.library_management_system.Notification;

import com.ardnaxela.library_management_system.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndReadFalse(User currentUser);

    List<Notification> findByUserOrderByCreatedAtDesc(User currentUser);

    List<Notification> findByNotificationTypeOrderByCreatedAtDesc(String notificationType);
}
