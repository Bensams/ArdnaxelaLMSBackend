package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;

import java.util.List;

public interface BorrowingService {
    void borrowBook(BorrowingDTO borrowingDTO);

    void approveBorrowing(Long borrowingId);
    void rejectBorrowing(Long borrowingId);

    void changeStatus(Long borrowingId, String status);

    void sendBorrowingStatusNotification(Borrowing borrowing);

    List<BorrowingDetailsDTO> getActiveBorrowingsByGuest(String guestName, String guestEmail, String guestPhone);
    void returnBookByGuest(Long borrowingId, String guestIdentifier);

    List<BorrowingDetailsDTO> getBorrowingHistoryByUsername(String username);

    List<BorrowingDetailsDTO> getBorrowingHistoryByUsernameAndStatus(String username, String status);
    boolean isBookAvailable(Long bookId);

     boolean isBookBorrowedByUser(BorrowingDTO borrowingDTO);

    List<BorrowingDetailsDTO> getAllBorrowings();

    void updateBorrowing(BorrowingDetailsDTO borrowingDetailsDTO);

    void deleteBorrowing(Long borrowingID);

    List<BorrowingDetailsDTO> getBorrowingsByStatus(String pending);
}
