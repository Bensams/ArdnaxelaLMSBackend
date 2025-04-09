package com.ardnaxela.library_management_system.Notification;

import com.ardnaxela.library_management_system.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndReadFalse(User currentUser);

    Collection<Object> findByUserOrderByCreatedAtDesc(User currentUser);
}
