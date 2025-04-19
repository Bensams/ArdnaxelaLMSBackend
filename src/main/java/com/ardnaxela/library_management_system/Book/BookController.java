package com.ardnaxela.library_management_system.Book;

import com.ardnaxela.library_management_system.Mapper.BookMapper;
import com.ardnaxela.library_management_system.Services.BookService;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {
    private final UserRepository userRepository;
    private BookService bookService;

    @Autowired
    public BookController(BookService bookService, UserRepository userRepository) {
        this.bookService = bookService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List> getAllBooks() {

        try {
            List<BookDTO> books = bookService.getAllBooks();
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable("id") Long bookId) {
        BookDTO bookDTO = bookService.getBookById(bookId);

        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookDTO bookDTO) {
        BookDTO savedBook = bookService.addBook(bookDTO);

        return new ResponseEntity<>("Book Added Successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<BookDTO>> createBooks(@RequestBody List<BookDTO> bookDTOs) {
        List<BookDTO> savedBooks = bookService.addBooks(bookDTOs);

        return new ResponseEntity<>(savedBooks, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") Long bookId,
                                              @RequestBody BookDTO updatedBook) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }

        bookService.updateBook(bookId, updatedBook);

        return new ResponseEntity<>("Book Updated Successfuly!", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") Long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (!currentUser.getRole().contains("LIBRARIAN") && !currentUser.getRole().contains("ADMIN")) {
            throw new AccessDeniedException("Permission denied");
        }
//
//        // Check if the user has admin role
//        if (!authentication.getAuthorities().stream()
//                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
//            return new ResponseEntity<>("You don't have access to this", HttpStatus.FORBIDDEN);
//        }
        bookService.deleteBook(bookId);

        return ResponseEntity.ok("Book deleted successfully!");
    }

}
