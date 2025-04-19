package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Book.BookDTO;
import com.ardnaxela.library_management_system.Book.BookRepository;
import com.ardnaxela.library_management_system.Borrowing.BorrowingRepository;
import com.ardnaxela.library_management_system.Exceptions.ForbiddenException;
import com.ardnaxela.library_management_system.Exceptions.InvalidIsbnFormatException;
import com.ardnaxela.library_management_system.Exceptions.ResourceNotFoundException;
import com.ardnaxela.library_management_system.Mapper.BookMapper;
import com.ardnaxela.library_management_system.Notification.NotificationEvent;
import com.ardnaxela.library_management_system.Services.BookService;
import com.ardnaxela.library_management_system.Services.NotificationPublisher;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
    private final NotificationPublisher notificationPublisher;
    private UserRepository userRepository;
    private BorrowingRepository borrowingRepository;
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{3}-\\d-\\d{3}-\\d{5}-\\d|\\d{13})$");

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(BookMapper::toBookDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        // Only validate ISBN if it's provided and not empty
        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().isEmpty()) {
            if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
                throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
            }
            if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
                throw new ForbiddenException("A book with the given ISBN already exists: "
                        + bookDTO.getIsbn() + ". Title: " + bookDTO.getTitle());
            }
        }

        Book book = BookMapper.toBookEntity(bookDTO);
        Book savedBook = bookRepository.save(book);

        notifyAllUsersAboutNewBook(book);

        return BookMapper.toBookDTO(savedBook);
    }

    @Override
    public List<BookDTO> addBooks(List<BookDTO> books) {
        return books.stream()
                .map(bookDTO -> {
                    try {
                        // Only validate if ISBN exists and is not empty
                        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().isEmpty()) {
                            if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
                                throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
                            }
                            if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
                                throw new ForbiddenException("A book with the given ISBN already exists: "
                                        + bookDTO.getIsbn() + ". Title: " + bookDTO.getTitle());
                            }
                        }

                        Book book = BookMapper.toBookEntity(bookDTO);
                        Book savedBook = bookRepository.save(book);
                        return BookMapper.toBookDTO(savedBook);
                    } catch (InvalidIsbnFormatException | ForbiddenException e) {
                        System.err.println("Error processing book: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        return BookMapper.toBookDTO(book);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        // Only validate if ISBN exists and is not empty
        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().isEmpty()) {
            if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
                throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
            }
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn() != null ? bookDTO.getIsbn().replace("-", "") : null);
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

        // Check if the book is borrowed
        borrowingRepository.findByBookId(id)
                .ifPresent(borrowing -> {
                    throw new ForbiddenException("Cannot delete book that is currently borrowed.");
                });

        bookRepository.deleteById(book.getId());
    }

    @Override
    public void notifyAllUsersAboutNewBook(Book book) {
        NotificationEvent event = new NotificationEvent();
        event.setType("NEW_BOOK_ARRIVAL");
        event.setMessage("Title: " + book.getTitle()
                        +"\nAuthor: " + book.getAuthor()
        +"\nISBN: " + book.getIsbn());
        notificationPublisher.publishNotification(event);
    }

    }

//    public boolean isBookAvailable(Long id) {
//        Book book = bookRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
//
//        return book.getQuantity() > 0;
//    }


