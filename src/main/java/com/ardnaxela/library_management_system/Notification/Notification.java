package com.ardnaxela.library_management_system.Notification;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.User.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id") // "user_id" is the foreign key column
    private User user;

    @OneToOne
    @JoinColumn(name = "book_id", nullable =false)
    private Book book;

    @Column(name = "notification_type",nullable = false, length = 100)
    private String notificationType;

    private String message;

    // add read
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
