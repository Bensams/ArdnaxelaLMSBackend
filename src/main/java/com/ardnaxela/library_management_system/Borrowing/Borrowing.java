package com.ardnaxela.library_management_system.Borrowing;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Member.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

@Data
@Entity
@Table(name = "borrowings")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // Reference to the Book entity (Foreign Key)

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // Reference to the Member entity (Foreign Key, nullable for guests)

    @Column(name = "guest_name", length = 255)
    private String guestName; // Name of the guest member

    @Column(name = "guest_email", length = 255)
    private String guestEmail; // Email of the guest member

    @Column(name = "guest_phone_number", length = 15)
    private String guestPhoneNumber; // Phone number of the guest member

    @Column(length = 50)
    private String status = "pending"; // Status of the transaction (pending, approved, rejected, returned, overdue, cancelled)

    @Column(name = "borrowed_date", nullable = false)
//    @JsonFormat(pattern = "MM/dd/yyyy") // Format "MM/dd/YYYY"
    private LocalDateTime borrowedDate; // Date the book was borrowed

    @Column(name = "due_date", nullable = false)
//    @JsonFormat(pattern = "MM/dd/yyyy") // Format "MM/dd/YYYY"
    private LocalDateTime dueDate; // Due date for returning the book

    @Column(name = "returned_date")
//    @JsonFormat(pattern = "MM/dd/yyyy") // Format "MM/dd/YYYY"
    private LocalDateTime returnedDate; // Date the book was returned (nullable)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Timestamp for creation

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Timestamp for last update

    @PrePersist
    protected void onCreate() {
        if ( status == null || status.isEmpty() ) {
            status = "pending"; // Default status
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}