package com.ardnaxela.library_management_system.Borrowing;

import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Services.BorrowingService;
import com.ardnaxela.library_management_system.Services.NotificationService;
import com.ardnaxela.library_management_system.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BorrowingController {
    // Add your methods here for handling borrowing-related requests
    // For example, you can add methods to create, update, delete, and retrieve borrowings
    private final BorrowingService borrowingService;
    private final NotificationService notificationService;

    @GetMapping("/pending")
    public ResponseEntity<List<BorrowingDetailsDTO>> getPendingBorrowings() {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }

        List<BorrowingDetailsDTO> borrowings = borrowingService.getBorrowingsByStatus("PENDING");
        return new ResponseEntity<>(borrowings, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BorrowingDetailsDTO>> getAllBorrowings() {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        // Implement the logic to retrieve a borrowing
        // For example, you can call a service method to get the borrowing from the database
        List<BorrowingDetailsDTO> borrowings = borrowingService.getAllBorrowings();

        return new ResponseEntity<>(borrowings, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateBorrowing(@RequestBody BorrowingDetailsDTO borrowingDetailsDTO) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        // Implement the logic to create a borrowing
        // For example, you can call a service method to save the borrowing in the database
        // Implement Notifications
        borrowingService.updateBorrowing(borrowingDetailsDTO);

        return new ResponseEntity<>("Borrowing Updated Successfully!", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBorrowing(@PathVariable("id") Long borrowingId) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        // Implement the logic to delete a borrowing
        // For example, you can call a service method to delete the borrowing from the database
        // Implement Notifications
        borrowingService.deleteBorrowing(borrowingId);

        return new ResponseEntity<>("Borrowing Deleted Successfully!", HttpStatus.OK);
    }

    @PatchMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<String> approveBorrowing(@PathVariable("id") Long borrowingId) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        // Implement the logic to update a borrowing
        // For example, you can call a service method to update the borrowing in the database
        // Implement Notifications
        borrowingService.approveBorrowing(borrowingId);

        return new ResponseEntity<>("Borrowing Has been approved!", HttpStatus.OK);
    }

    @PatchMapping("reject/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<String> rejectBorrowing(@PathVariable("id") Long borrowingId) {
        User currentUser = notificationService.getCurrentUser();
        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
        // Implement the logic to update a borrowing
        // For example, you can call a service method to update the borrowing in the database
        // Implement Notifications
        borrowingService.rejectBorrowing(borrowingId);

        return new ResponseEntity<>( "Borrowing Has been rejected!",  HttpStatus.OK);
    }



    // Example method
    // @GetMapping
    // public ResponseEntity<List<BorrowingDTO>> getAllBorrowings() {
    //     List<BorrowingDTO> borrowings = borrowingService.getAllBorrowings();
    //     return new ResponseEntity<>(borrowings, HttpStatus.OK);
    // }
}
