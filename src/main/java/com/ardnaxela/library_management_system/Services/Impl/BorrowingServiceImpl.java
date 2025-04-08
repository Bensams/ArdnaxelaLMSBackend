package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.Borrowing.BorrowingRepository;

import com.ardnaxela.library_management_system.Services.BorrowingService;
import com.ardnaxela.library_management_system.Mapper.BorrowingMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {
    private final BorrowingRepository borrowingRepository;

    @Override
    public void borrowBook(BorrowingDTO borrowingDTO) {
        // Check if the member exists or guest Name
        if (borrowingDTO.getMemberId() == null && borrowingDTO.getGuestName() == null) {
            throw new RuntimeException("Guest name must be provided.");
        }



        // Check if the book is available for borrowing
        Borrowing borrowing = BorrowingMapper.toEntity((BorrowingDTO) borrowingDTO);
        if (isBookAvailable(borrowing.getId())) {
            borrowingRepository.save(borrowing);
        } else {
            throw new RuntimeException("Book is not available for borrowing.");
        }
    }

//    @Override
//    public void returnBook(BorrowingDTO borrowingDTO) {
//        // Check if the borrowing record exists or use Borrow Number to check
//        Borrowing borrowing = borrowingRepository.findById(borrowingDTO.getId())
//                .or(() -> borrowingRepository.findByGuestName(borrowingDTO.getGuestName()))
//                .or(() -> borrowingRepository.findByMemberId(borrowingDTO.getMemberId()))
//                .orElseThrow(() -> new RuntimeException("Borrowing record not found."));
//        // Update the borrowing record to mark it as returned
//        borrowing.setReturnedDate(BorrowingMapper.toEntity(borrowingDTO).getReturnedDate());
//        borrowing.setStatus("RETURNED");
//    }
@Override
public void returnBook(BorrowingDTO borrowingDTO) {
    // Validate input
    if (borrowingDTO == null) {
        throw new IllegalArgumentException("BorrowingDTO cannot be null");
    }

    // Find the borrowing record using the most specific identifier first
    Borrowing borrowing = borrowingRepository.findById(borrowingDTO.getId())
            .or(() -> borrowingRepository.findById(borrowingDTO.getId()))
            .or(() -> {
                if (borrowingDTO.getMemberId() != null) {
                    return borrowingRepository.findFirstByMemberIdAndStatusOrderByBorrowedDateDesc(
                            borrowingDTO.getMemberId(), "BORROWED");
                }
                return Optional.empty();
            })
            .or(() -> {
                if (borrowingDTO.getGuestName() != null) {
                    return borrowingRepository.findFirstByGuestNameAndStatusOrderByBorrowedDateDesc(
                            borrowingDTO.getGuestName(), "BORROWED");
                }
                return Optional.empty();
            })
            .orElseThrow(() -> new EntityNotFoundException(
                    "No active borrowing record found for the given criteria"));

    // Validate the borrowing isn't already returned
    if ("RETURNED".equals(borrowing.getStatus())) {
        throw new IllegalStateException("This book has already been returned");
    }

    // Update the borrowing record
    borrowing.setReturnedDate(LocalDateTime.now());  // or borrowingDTO.getReturnedDate() if you prefer
    borrowing.setStatus("RETURNED");

    // Save the updated record
    borrowingRepository.save(borrowing);
}

    @Override
    public List<Borrowing> getBorrowingHistoryByUsername(String username) {
        // Find by username through member ID to User
        return List.of();
    }

    @Override
    public List<Borrowing> getBorrowingHistoryByGuest(String guestEmail, String guestPhoneNumber) {
        // Find by guest email and phone number
        return borrowingRepository.findByGuestEmail(guestEmail)
                .or(() -> borrowingRepository.findByGuestPhoneNumber(guestPhoneNumber))
                .map(b -> List.of((Borrowing) b))
                .orElseThrow(() -> new RuntimeException("Borrowing record not found."));
    }

    @Override
    public boolean isBookAvailable(Long bookId) {
        // Check if the book is available for borrowing
        Optional<Borrowing> borrowing = borrowingRepository.findByBookId(bookId);
        return borrowing.isEmpty() || "RETURNED".equals(borrowing.get().getStatus());
    }


    @Override
    public boolean isBookBorrowedByUser(BorrowingDTO borrowingDTO) {
        // Check if the book is borrowed by the user or guest
        Optional<Borrowing> borrowing = borrowingRepository.findByBookIdAndMemberId(borrowingDTO.getBookId(), borrowingDTO.getMemberId());
        if (borrowing.isPresent()) {
            return "BORROWED".equals(borrowing.get().getStatus());
        } else {
            final Boolean b1 = borrowingRepository.findByBookIdAndGuestName(borrowingDTO.getBookId(), borrowingDTO.getGuestName())
                    .map(b -> "BORROWED".equals(((Borrowing) b).getStatus()))
                    .orElse(false);
            return b1;
        }


    }
}
