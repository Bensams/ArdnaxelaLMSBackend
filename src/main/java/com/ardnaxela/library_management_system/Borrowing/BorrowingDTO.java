package com.ardnaxela.library_management_system.Borrowing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingDTO {
    private Long id;
    private Long bookId;
    private Long memberId;
    private String guestName; // For guest users
    private String guestEmail; // For guest users
    private String guestPhone; // For guest users
    private String status; // e.g., "BORROWED", "RETURNED", "OVERDUE", PENDING, CANCELLED
    private String borrowDate;
    private String dueDate;
    private String returnedDate;
}
