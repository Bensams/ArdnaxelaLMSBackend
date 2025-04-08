package com.ardnaxela.library_management_system.Mapper;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Book.BookDTO;

public class BookMapper {
    public static BookDTO toBookDTO(Book book) {
        if (book == null) {
            return null;
        }
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setIsbn(book.getIsbn());
        bookDTO.setPublishedYear(book.getPublishedYear());
        bookDTO.setCategory(book.getCategory());
        bookDTO.setQuantity(book.getQuantity());


        return bookDTO;
    }

    public static Book toBookEntity(BookDTO bookDTO) {
        if (bookDTO == null) {
            return null;
        }
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublishedYear(bookDTO.getPublishedYear());
        book.setCategory(bookDTO.getCategory());
        book.setQuantity(bookDTO.getQuantity());

        return book;
    }
}
