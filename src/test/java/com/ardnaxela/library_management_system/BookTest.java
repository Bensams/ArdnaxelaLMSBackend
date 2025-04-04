package com.ardnaxela.library_management_system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.ardnaxela.library_management_system.Book.Book;

public class BookTest {

    @Test
    public void testSetTitle() {
        Book book = new Book();
        book.setTitle("Effective Java");
        assertEquals("Effective Java", book.getTitle());
    }

    @Test
    public void testSetAuthor() {
        Book book = new Book();
        book.setAuthor("Joshua Bloch");
        assertEquals("Joshua Bloch", book.getAuthor());
    }

    @Test
    public void testSetIsbn() {
        Book book = new Book();
        book.setIsbn("978-0134685991");
        assertEquals("978-0134685991", book.getIsbn());
    }

    @Test
    public void testSetPublishedYear() {
        Book book = new Book();
        book.setPublishedYear(2018);
        assertEquals(2018, book.getPublishedYear());
    }

    @Test
    public void testSetQuantity() {
        Book book = new Book();
        book.setQuantity(10);
        assertEquals(10, book.getQuantity());
    }
}
