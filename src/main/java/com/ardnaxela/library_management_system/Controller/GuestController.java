package com.ardnaxela.library_management_system.Controller;

import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Services.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@CrossOrigin("*")
public class GuestController {

    private final BorrowingService borrowingService;

    @GetMapping("/search")
    public ResponseEntity<List<BorrowingDetailsDTO>> searchActiveBorrowings(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {

        if ((name == null || name.isBlank()) &&
                (email == null || email.isBlank()) &&
                (phone == null || phone.isBlank())) {
            return ResponseEntity.badRequest().build();
        }

        List<BorrowingDetailsDTO> borrowings = borrowingService.getActiveBorrowingsByGuest(
                name != null ? name : "",
                email != null ? email : "",
                phone != null ? phone : "");

        return ResponseEntity.ok(borrowings);
    }

    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestBody BorrowingDTO borrowingDTO) {

        borrowingService.borrowBook(borrowingDTO);
        return ResponseEntity.ok("Book borrowed successfully");
    }

    // Planning on management to use
    @PostMapping("/return/{borrowingId}")
    public ResponseEntity<String> returnBook(
            @PathVariable Long borrowingId,
            @RequestParam String guestIdentifier) {

        borrowingService.returnBookByGuest(borrowingId, guestIdentifier);
        return ResponseEntity.ok("Book returned successfully");
    }
}