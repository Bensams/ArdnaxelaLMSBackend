package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;

import java.util.List;

public interface BorrowingService {
    void borrowBook(BorrowingDTO borrowingDTO);

    void returnBook(BorrowingDTO borrowingDTO);
//
//    void renewBook(Long bookId, String username);
//
//    void reserveBook(Long bookId, String username);
//
//    void cancelReservation(Long bookId, String username);
    List<Borrowing> getBorrowingHistoryByUsername(String username);
    List<Borrowing> getBorrowingHistoryByGuest(String guestEmail, String guestPhoneNumber);
//
    boolean isBookAvailable(Long bookId);
//
//    boolean isBookReserved(Long bookId, String username);
//
    boolean isBookBorrowedByUser(BorrowingDTO borrowingDTO);
}
