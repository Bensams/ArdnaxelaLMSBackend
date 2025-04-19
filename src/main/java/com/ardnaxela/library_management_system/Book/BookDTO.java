package com.ardnaxela.library_management_system.Book;


import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    @Nullable
    private String isbn;
    private Integer publishedYear;
    private String category;
    private Integer quantity;

}
