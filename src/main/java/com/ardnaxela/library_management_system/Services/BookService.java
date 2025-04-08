package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Book.BookDTO;
import com.ardnaxela.library_management_system.Book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface BookService  {

    List<BookDTO> getAllBooks();

    BookDTO addBook(BookDTO bookDTO);

    List<BookDTO> addBooks(List<BookDTO> books);

    BookDTO getBookById(Long id);

    BookDTO updateBook(Long id, BookDTO bookDTO);

    void deleteBook(Long id);
}
