package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Book.BookDTO;
import com.ardnaxela.library_management_system.Book.BookRepository;
import com.ardnaxela.library_management_system.Exceptions.ForbiddenException;
import com.ardnaxela.library_management_system.Exceptions.InvalidIsbnFormatException;
import com.ardnaxela.library_management_system.Exceptions.ResourceNotFoundException;
import com.ardnaxela.library_management_system.Mapper.BookMapper;
import com.ardnaxela.library_management_system.Services.BookService;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{3}-\\d-\\d{3}-\\d{5}-\\d|\\d{13})$");

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(BookMapper::toBookDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
            throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
        }

        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
//            throw new InvalidIsbnFormatException("Book with ISBN " + bookDTO.getIsbn() + " already exists.");

            throw new ForbiddenException("A book with the given ISBN already exists: "
                    + bookDTO.getIsbn() + ". Title: " + bookDTO.getTitle());
        }

        Book book = BookMapper.toBookEntity(bookDTO);
        Book savedBook = bookRepository.save(book);

        return BookMapper.toBookDTO(savedBook);
    }

    @Override
    public List<BookDTO> addBooks(List<BookDTO> books) {
        List<BookDTO> successfullyAddedBooks = books.stream()
                .map(bookDTO -> {
                    try {
                        // Check ISBN and handle if invalid
                        if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
                            throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
                        }

                        // Check for duplicate ISBNs
                        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
                            throw new ForbiddenException("A book with the given ISBN already exists: "
                                    + bookDTO.getIsbn() + ". Title: " + bookDTO.getTitle());
                        }

                        // If no exception, convert DTO to entity and return DTO of saved entity
                        Book book = BookMapper.toBookEntity(bookDTO);
                        Book savedBook = bookRepository.save(book);

                        return BookMapper.toBookDTO(savedBook);
                    } catch (InvalidIsbnFormatException | ForbiddenException e) {
                        // Log error and return null or skip
                        System.err.println("Error processing book: " + e.getMessage());
                        return null; // Returning null to exclude this invalid book
                    }
                })
                .filter(bookDTO -> bookDTO != null) // Exclude null entries from the final result
                .collect(Collectors.toList());

        return successfullyAddedBooks;
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        return BookMapper.toBookDTO(book);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
            throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
        }

        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
//            throw new InvalidIsbnFormatException("Book with ISBN " + bookDTO.getIsbn() + " already exists.");

            throw new ForbiddenException("A book with the given ISBN already exists: "
                    + bookDTO.getIsbn() + ". Title: " + bookDTO.getTitle());
        }

        // How to remove "-" to isbn before change?

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn().replace("-", ""));
        book.setPublishedYear(bookDTO.getPublishedYear());
        book.setCategory(bookDTO.getCategory());
        book.setQuantity(bookDTO.getQuantity());

        Book updatedBook = bookRepository.save(book);

        return BookMapper.toBookDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        bookRepository.deleteById(id);
    }

//    public boolean isBookAvailable(Long id) {
//        Book book = bookRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
//
//        return book.getQuantity() > 0;
//    }


}
