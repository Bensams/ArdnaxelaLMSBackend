package com.ardnaxela.library_management_system.DTO;

import com.ardnaxela.library_management_system.Book.BookDTO;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingDetailsDTO {
    private Long id;
    private BookDTO book;
    private MemberDTO member;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String status;
    private String borrowDate;
    private String dueDate;
    private String returnedDate;
}