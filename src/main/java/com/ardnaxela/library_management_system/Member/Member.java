package com.ardnaxela.library_management_system.Member;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import com.ardnaxela.library_management_system.User.User;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Establish ManyToOne or OneToOne relationship with the User entity
    @OneToOne // or @OneToOne based on your requirements
    @JoinColumn(name = "user_id", nullable = false) // "user_id" is the foreign key column
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Pattern(regexp="^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message="Invalid email format")
    @Column(nullable=false, length = 255)
    private String email;

    @Column(nullable = false, name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

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
