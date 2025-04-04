package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Book.BookDTO;
import com.ardnaxela.library_management_system.Book.BookRepository;
import com.ardnaxela.library_management_system.Exceptions.InvalidIsbnFormatException;
import com.ardnaxela.library_management_system.Exceptions.ResourceNotFoundException;
import com.ardnaxela.library_management_system.Mapper.BookMapper;
import com.ardnaxela.library_management_system.Services.BookService;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{3}-\\d{10}|\\d{13})$");

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(BookMapper::mapToBookDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
            throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
        }

        Book book = BookMapper.mapToBookEntity(bookDTO);
        Book savedBook = bookRepository.save(book);

        return BookMapper.mapToBookDTO(savedBook);
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        return BookMapper.mapToBookDTO(book);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        if (!ISBN_PATTERN.matcher(bookDTO.getIsbn()).matches()) {
            throw new InvalidIsbnFormatException("Invalid ISBN format: " + bookDTO.getIsbn());
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublishedYear(bookDTO.getPublishedYear());
        book.setQuantity(bookDTO.getQuantity());

        Book updatedBook = bookRepository.save(book);

        return BookMapper.mapToBookDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        bookRepository.deleteById(id);
    }


}
