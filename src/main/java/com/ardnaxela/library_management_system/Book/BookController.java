package com.ardnaxela.library_management_system.Book;

import com.ardnaxela.library_management_system.Mapper.BookMapper;
import com.ardnaxela.library_management_system.Services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {
    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
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
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        BookDTO savedBook = bookService.addBook(bookDTO);

        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<BookDTO>> createBooks(@RequestBody List<BookDTO> bookDTOs) {
        List<BookDTO> savedBooks = bookService.addBooks(bookDTOs);

        return new ResponseEntity<>(savedBooks, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable("id") Long bookId,
                                              @RequestBody BookDTO updatedBook) {
        BookDTO bookDTO = bookService.updateBook(bookId, updatedBook);

        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") Long bookId) {
        bookService.deleteBook(bookId);

        return ResponseEntity.ok("Book deleted successfully!");
    }

}
