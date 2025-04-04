package com.ardnaxela.library_management_system.Book;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(nullable = false, length = 13, unique = true) // ISBN-13 is 13 digits long and unique
    @Pattern(regexp = "^(\\d{3}-\\d{10}|\\d{13})$", message = "Invalid ISBN format. Use either '123-1234567891' or '1231234567891'.")
    private String isbn;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (isbn != null) {
            isbn = isbn.replace("-", ""); // Remove hyphens for consistency
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
