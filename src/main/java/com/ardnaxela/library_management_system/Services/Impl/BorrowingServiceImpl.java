package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Book.BookRepository;
import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.Borrowing.BorrowingRepository;

import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Mapper.BookMapper;
import com.ardnaxela.library_management_system.Mapper.DateTimeMapper;
import com.ardnaxela.library_management_system.Member.Member;
import com.ardnaxela.library_management_system.Member.MemberRepository;
import com.ardnaxela.library_management_system.Notification.NotificationDTO;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.BorrowingService;
import com.ardnaxela.library_management_system.Mapper.BorrowingMapper;
import com.ardnaxela.library_management_system.Services.NotificationPublisher;
import com.ardnaxela.library_management_system.Services.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {
    private final MemberRepository memberRepository;
    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final NotificationPublisher notificationPublisher;
    private final NotificationService notificationService;

    // Add to BorrowingServiceImpl.java

    @Override
    public List<BorrowingDetailsDTO> getActiveBorrowingsByGuest(String guestName, String guestEmail, String guestPhone) {
        // Validate at least one search criteria is provided
        if ((guestName == null || guestName.isBlank()) &&
                (guestEmail == null || guestEmail.isBlank()) &&
                (guestPhone == null || guestPhone.isBlank())) {
            throw new IllegalArgumentException("At least one guest identifier (name, email, or phone) must be provided.");
        }

        List<Borrowing> borrowings = null;

        // Search by name first if provided
        if (guestName != null && !guestName.isBlank()) {
            borrowings = borrowingRepository.findByGuestNameContainingIgnoreCaseAndStatusNot(guestName, "");
        }

        // If no results from name search, try email
        if ((borrowings == null || borrowings.isEmpty()) &&
                guestEmail != null && !guestEmail.isBlank()) {
            borrowings = borrowingRepository.findByGuestEmailContainingIgnoreCaseAndStatusNot(guestEmail, "");
        }

        // If still no results, try phone
        if ((borrowings == null || borrowings.isEmpty()) &&
                guestPhone != null && !guestPhone.isBlank()) {
            borrowings = borrowingRepository.findByGuestPhoneNumberContainingAndStatusNot(guestPhone, "");
        }

        // If still no results found
        if (borrowings == null || borrowings.isEmpty()) {
            throw new EntityNotFoundException("No active borrowings found for the provided guest information.");
        }

        // Convert to DTOs
        return borrowings.stream()
                .map(BorrowingMapper::toDetailsDTO)
                .toList();
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
        // Validate the borrowing request
        if (borrowingDTO.getBookId() == null) {
            throw new IllegalArgumentException("Book must be specified");
        }

        // Check if either member or guest information is provided
        if (borrowingDTO.getMemberId() == null &&
                (borrowingDTO.getGuestName() == null || borrowingDTO.getGuestName().isBlank())) {
            throw new IllegalArgumentException("Either member or guest name must be provided");
        }

        // Validate guest contact info if this is a guest borrowing
        if (borrowingDTO.getMemberId() == null &&
                (borrowingDTO.getGuestEmail() == null || borrowingDTO.getGuestEmail().isBlank()) &&
                (borrowingDTO.getGuestPhone() == null || borrowingDTO.getGuestPhone().isBlank())) {
            throw new IllegalArgumentException("Guest must provide either email or phone number");
        }

        // Check book availability
        if (!isBookAvailable(borrowingDTO.getBookId())) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        // Create and save the borrowing record
        Borrowing borrowing = BorrowingMapper.toEntity(borrowingDTO);
        borrowing.setStatus("PENDING");
        borrowing.setBorrowedDate(LocalDateTime.now());

        // Set due date (default to 1 week from now if not provided)
        if (borrowingDTO.getDueDate() == null) {
            borrowing.setDueDate(LocalDateTime.now().plusWeeks(1));
        } else {
            borrowing.setDueDate(DateTimeMapper.toLocalDateTime(borrowingDTO.getDueDate()));
        }

        borrowingRepository.save(borrowing);

    }

    @Override
    public void approveBorrowing(Long borrowingId) {

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing not found"));

        if ("PENDING".equals(borrowing.getStatus())) {
            borrowing.setStatus("APPROVED");
            borrowingRepository.save(borrowing);

            sendBorrowingStatusNotification(borrowing);
        } else if ("APPROVED".equals(borrowing.getStatus())) {
            throw new IllegalStateException("This borrowing has already been approved.");
        } else {
            throw new IllegalStateException("This borrowing has already been transacted.");
        }

    }

    @Override
    public void rejectBorrowing(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing not found"));

        if ("PENDING".equals(borrowing.getStatus())) {
            borrowing.setStatus("REJECTED");
            borrowingRepository.save(borrowing);

            sendBorrowingStatusNotification(borrowing);
        } else if ("REJECTED".equals(borrowing.getStatus())) {
            throw new IllegalStateException("This borrowing has already been rejected.");
        }else {
            throw new IllegalStateException("This borrowing has already been transacted.");
        }

    }

    @Override
    public void changeStatus(Long borrowingId, String status) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing not found"));

        if ("APPROVED".equals(status))
            approveBorrowing(borrowingId);
        else if ("REJECTED".equals(status))
            rejectBorrowing(borrowingId);
        else if ("RETURNED".equals(status)) {
            if ("RETURNED".equals(borrowing.getStatus())) {
                return;
            }
            borrowing.setStatus("RETURNED");
            borrowing.setReturnedDate(LocalDateTime.now());
            borrowingRepository.save(borrowing);

            // Increase book quantity
            Book book = bookRepository.findById(borrowing.getBook().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found"));
            book.setQuantity(book.getQuantity() + 1);
            bookRepository.save(book);
        } else if ("CANCELLED".equals(status)) {
            if ("CANCELLED".equals(borrowing.getStatus())) {
                return;
            }
            borrowing.setStatus("CANCELLED");
            borrowingRepository.save(borrowing);
        } else if ("OVERDUE".equals(status)) {
            if ("OVERDUE".equals(borrowing.getStatus())) {
                return;
            }
            borrowing.setStatus("OVERDUE");
            borrowingRepository.save(borrowing);
        } else if ("BORROWED".equals(status)) {
            if ("BORROWED".equals(borrowing.getStatus())) {
                return;
            }
            borrowing.setStatus("BORROWED");
            borrowing.setBorrowedDate(LocalDateTime.now());
            borrowingRepository.save(borrowing);

            // Decrease book quantity
            Book book = bookRepository.findById(borrowing.getBook().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Book not found"));
            book.setQuantity(book.getQuantity() - 1);
            bookRepository.save(book);
        } else if ("PENDING".equals(status)) {
            if ("PENDING".equals(borrowing.getStatus())) {
                return;
            }
            borrowing.setStatus("PENDING");
            borrowingRepository.save(borrowing);
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Override
    public void sendBorrowingStatusNotification(Borrowing borrowing) {

        String status = borrowing.getStatus();
        NotificationEvent event = new NotificationEvent();
        event.setBorrowingId(borrowing.getId());
        event.setType(status);
        event.setMessage("Your borrowing request for '" + borrowing.getBook().getTitle() +
                "' has been '" + status + "'." + ((status == "APPROVED") ?
                " Please proceed to the counter to claim your book!" : ""));

        if (borrowing.getMember() == null) {
            // Use guest contact preferences
            event.setEmailPreferred(borrowing.getGuestEmail() != null);
            event.setSmsPreferred(borrowing.getGuestPhoneNumber() != null);
        } else {
            // For members
            event.setEmailPreferred(borrowing.getMember().getUser().isEmailNotificationsEnabled());
            event.setSmsPreferred(borrowing.getMember().getUser().isSmsNotificationsEnabled());
        }

        notificationPublisher.publishNotification(event);
    }

//    @Override
//    public void sendBorrowingStatusNotification(Borrowing borrowing, String status) {
//        NotificationEvent event = new NotificationEvent();
//
//        if (borrowing.getMember() != null) {
//            event.setUserId(borrowing.getMember().getUser().getId());
//        } else {
//            // For guest users, we can't send web notifications but can send email/SMS
//            event.setUserId(null); // Or handle differently
//        }
//
//        event.setBookId(borrowing.getBook().getId());
//        event.setMessage("Your borrowing request for '" + borrowing.getBook().getTitle() +
//                "' has been '" + status + "'");
//        event.setType(status.equals("APPROVED") ?
//                NotificationEvent.NotificationType.BORROWING_APPROVED :
//                NotificationEvent.NotificationType.BORROWING_REJECTED);
//
//        NotificationDTO notificationDTO = new NotificationDTO();
//        notificationDTO.setUserID(event.getUserId());
//        notificationDTO.setBookID(event.getBookId());
//        notificationDTO.setMessage(event.getMessage());
//        notificationDTO.setNotificationType(event.getType().toString());
//        notificationDTO.setBook(BookMapper.toBookDTO(borrowing.getBook()));
//
//        notificationService.createNotification(notificationDTO);
//
//        // For members
//        if (borrowing.getMember() != null) {
//            User user = borrowing.getMember().getUser();
//            event.setEmailPreferred(user.isEmailNotificationsEnabled());
//            event.setSmsPreferred(user.isSmsNotificationsEnabled());
//        } else {
//            // For guests, use the contact info they provided
//            event.setEmailPreferred(borrowing.getGuestEmail() != null);
//            event.setSmsPreferred(borrowing.getGuestPhoneNumber() != null);
//        }
//
//        notificationPublisher.publishNotification(event);
//    }

    @Override
    public List<BorrowingDetailsDTO> getBorrowingHistoryByUsername(String username) {
        // Find by username through member ID to User
        // Get memberID through username
        Member member = memberRepository.findByUsername(username);

        return borrowingRepository.findByMemberId(member.getId())
                .stream()
                .map(BorrowingMapper::toDetailsDTO)
                .toList();
    }

    @Override
    public List<BorrowingDetailsDTO> getBorrowingHistoryByUsernameAndStatus(String username, String status) {
        // Find by username through member ID to User
        // Get memberID through username
        Member member = memberRepository.findByUsername(username);

        List<Borrowing> borrowedData = borrowingRepository.findByMemberIdAndStatus(member.getId(), status);

        if (borrowedData.isEmpty()) {
            throw new EntityNotFoundException("No borrowing history found for the provided username and status.");
        }
        return borrowedData.stream()
                .map(BorrowingMapper::toDetailsDTO)
                .toList();

    }


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

    @Override
    public List<BorrowingDetailsDTO> getAllBorrowings() {
        List<Borrowing> borrowings = borrowingRepository.findAll();
        if (borrowings.isEmpty()) {
            throw new EntityNotFoundException("No borrowings found");
        }
        return borrowings.stream()
                .map(BorrowingMapper::toDetailsDTO)
                .toList();
    }

    @Override
    public void updateBorrowing(BorrowingDetailsDTO borrowingDetailsDTO) {
        Borrowing borrowing = borrowingRepository.findById(borrowingDetailsDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Borrowing not found"));

        // Update the borrowing record with new values
        borrowing.setGuestName(borrowingDetailsDTO.getGuestName());
        borrowing.setGuestEmail(borrowingDetailsDTO.getGuestEmail());
        borrowing.setGuestPhoneNumber(borrowingDetailsDTO.getGuestPhone());

        borrowingRepository.save(borrowing);
        // Update the book if it has changed
        changeStatus(borrowing.getId(), borrowingDetailsDTO.getStatus());
    }

    @Override
    public void deleteBorrowing(Long borrowingID) {
        Borrowing borrowing = borrowingRepository.findById(borrowingID)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing not found"));
        // Check if the borrowing is borrowed
        if ("BORROWED".equals(borrowing.getStatus())) {
            throw new IllegalStateException("Cannot delete a borrowed borrowing record");
        } else {
            borrowingRepository.deleteById(borrowingID);
        }
    }

    @Override
    public List<BorrowingDetailsDTO> getBorrowingsByStatus(String status) {
        List<Borrowing> borrowings = borrowingRepository.findByStatus(status);
        return borrowings.stream()
                .map(BorrowingMapper::toDetailsDTO)
                .toList();

    }
}
