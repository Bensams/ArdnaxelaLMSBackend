package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Book.BookRepository;
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
    private final BookRepository bookRepository;

    // Add to BorrowingServiceImpl.java

    @Override
    public List<BorrowingDTO> getActiveBorrowingsByGuest(String guestName, String guestEmail, String guestPhone) {
        if ((guestName == null || guestName.isBlank()) &&
                (guestEmail == null || guestEmail.isBlank()) &&
                (guestPhone == null || guestPhone.isBlank())) {
            throw new IllegalArgumentException("At least one guest identifier (name, email, or phone) must be provided.");
        }

        return borrowingRepository.findByGuestName(guestName)
                .filter(list -> !list.isEmpty())
                .or(() -> borrowingRepository.findByGuestEmail(guestEmail)
                        .filter(list -> !list.isEmpty()))
                .or(() -> borrowingRepository.findByGuestPhoneNumber(guestPhone)
                        .filter(list -> !list.isEmpty()))
                .map(list -> list.stream()
                        .map(BorrowingMapper::toDTO)
                        .toList())
                .orElseThrow(() -> new EntityNotFoundException("No active borrowings found for the provided guest."));
    }

    @Override
    public void returnBookByGuest(Long borrowingId, String guestIdentifier) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing record not found"));

        // Verify guest identity
        if (!borrowing.getGuestName().equals(guestIdentifier) &&
                !borrowing.getGuestEmail().equals(guestIdentifier) &&
                !borrowing.getGuestPhoneNumber().equals(guestIdentifier)) {
            throw new SecurityException("Guest information does not match borrowing record");
        }

        if ("RETURNED".equals(borrowing.getStatus())) {
            throw new IllegalStateException("This book has already been returned");
        }

        borrowing.setReturnedDate(LocalDateTime.now());
        borrowing.setStatus("RETURNED");
        borrowingRepository.save(borrowing);
    }

    @Override
    public void borrowBook(BorrowingDTO borrowingDTO) {
        // Check if the member exists or guest Name
        if (borrowingDTO.getGuestName() == null) {
            throw new RuntimeException("Guest name must be provided.");
        }

        // Set the status to "PENDING"
        // Set the borrowedDate to the current date
        // Check if the book is available for borrowing
        Borrowing borrowing = BorrowingMapper.toEntity(borrowingDTO);
        borrowing.setStatus("PENDING");
        borrowing.setBorrowedDate(LocalDateTime.now());
        if (isBookAvailable(borrowing.getBook().getId())) {
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
//@Override
//public void returnBook(BorrowingDTO borrowingDTO) {
//    // Validate input
//    if (borrowingDTO == null) {
//        throw new IllegalArgumentException("BorrowingDTO cannot be null");
//    }
//
//    // Find the borrowing record using the most specific identifier first
//    Borrowing borrowing = borrowingRepository.findById(borrowingDTO.getId())
//            .or(() -> borrowingRepository.findById(borrowingDTO.getId()))
//            .or(() -> {
//                if (borrowingDTO.getMemberId() != null) {
//                    return borrowingRepository.findFirstByMemberIdAndStatusOrderByBorrowedDateDesc(
//                            borrowingDTO.getMemberId(), "BORROWED");
//                }
//                return Optional.empty();
//            })
//            .or(() -> {
//                if (borrowingDTO.getGuestName() != null) {
//                    return borrowingRepository.findFirstByGuestNameAndStatusOrderByBorrowedDateDesc(
//                            borrowingDTO.getGuestName(), "BORROWED");
//                }
//                return Optional.empty();
//            })
//            .orElseThrow(() -> new EntityNotFoundException(
//                    "No active borrowing record found for the given criteria"));
//
//    // Validate the borrowing isn't already returned
//    if ("RETURNED".equals(borrowing.getStatus())) {
//        throw new IllegalStateException("This book has already been returned");
//    }
//
//    // Update the borrowing record
//    borrowing.setReturnedDate(LocalDateTime.now());  // or borrowingDTO.getReturnedDate() if you prefer
//    borrowing.setStatus("RETURNED");
//
//    // Save the updated record
//    borrowingRepository.save(borrowing);
//}

    @Override
    public List<Borrowing> getBorrowingHistoryByUsername(String username) {
        // Find by username through member ID to User
        return List.of();
    }

//    @Override
//    public List<Borrowing> getBorrowingHistoryByGuest(String guestEmail, String guestPhoneNumber) {
//        // Find by guest email and phone number
//                return Optional.ofNullable(borrowingRepository.findByGuestEmail(guestEmail))
//                .or(() -> Optional.ofNullable(borrowingRepository.findByGuestPhoneNumber(guestPhoneNumber)))
//                .map(List::)
//                .orElseThrow(() -> new RuntimeException("Borrowing record not found."));
//    }

    @Override
    public boolean isBookAvailable(Long bookId) {
        // Check if the book is available for borrowing
        Optional<Book> book = bookRepository.findById(bookId);
        return book.get().getQuantity() > 0;
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
