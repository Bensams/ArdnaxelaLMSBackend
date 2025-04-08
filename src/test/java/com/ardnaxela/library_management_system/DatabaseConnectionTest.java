//package com.ardnaxela.library_management_system;
//
//import com.ardnaxela.library_management_system.Book.Book;
//import com.ardnaxela.library_management_system.Book.BookRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//public class DatabaseConnectionTest {
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    @Test
//    public void testDatabaseConnection() {
//        Book book = new Book();
//        book.setTitle("Test Book");
//        book.setAuthor("Test Author");
//        book.setIsbn("123-1234567891");
//        book.setPublishedYear(2023);
//        book.setQuantity(1);
//
//        Book savedBook = bookRepository.save(book);
//        assertNotNull(savedBook.getId(), "Book ID should not be null after saving to the database");
//    }
//}