package com.ardnaxela.library_management_system.Borrowing;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrowings")
@CrossOrigin(origins = "*")
public class BorrowingController {
    // Add your methods here for handling borrowing-related requests
    // For example, you can add methods to create, update, delete, and retrieve borrowings

    // Example method
    // @GetMapping
    // public ResponseEntity<List<BorrowingDTO>> getAllBorrowings() {
    //     List<BorrowingDTO> borrowings = borrowingService.getAllBorrowings();
    //     return new ResponseEntity<>(borrowings, HttpStatus.OK);
    // }
}
