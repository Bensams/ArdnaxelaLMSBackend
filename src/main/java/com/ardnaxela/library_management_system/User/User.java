package com.ardnaxela.library_management_system.User;

import com.ardnaxela.library_management_system.Member.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String role; // e.g., "ROLE_USER", "ROLE_ADMIN", "ROLE_LIBRARIAN"

    // Reverse relationship for bi-directional mapping (optional)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // Or @OneToOne if it's a one-to-one mapping
    private Member member; // Change to `Member member;` for a OneToOne relationship

}