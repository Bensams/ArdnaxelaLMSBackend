package com.ardnaxela.library_management_system.Mapper;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Member.Member;

public class BorrowingMapper {

    public static BorrowingDTO toDTO(Borrowing borrowing) {
        if (borrowing == null) {
            return null;
        }
        BorrowingDTO borrowingDTO = new BorrowingDTO();
        borrowingDTO.setId(borrowing.getId());

        // Set book ID instead of full BookDTO
        borrowingDTO.setBookId(borrowing.getBook() != null ? borrowing.getBook().getId() : null);

        // Set member ID instead of full Member
        borrowingDTO.setMemberId(borrowing.getMember() != null ? borrowing.getMember().getId() : null);

        borrowingDTO.setGuestName(borrowing.getGuestName());
        borrowingDTO.setGuestEmail(borrowing.getGuestEmail());
        borrowingDTO.setGuestPhone(borrowing.getGuestPhoneNumber());
        borrowingDTO.setStatus(borrowing.getStatus());
        borrowingDTO.setBorrowDate(DateTimeMapper.toString(borrowing.getBorrowedDate()));
        borrowingDTO.setDueDate(DateTimeMapper.toString(borrowing.getDueDate()));
        borrowingDTO.setReturnedDate(DateTimeMapper.toString(borrowing.getReturnedDate()));

        return borrowingDTO;
    }

    // Add this to your BorrowingMapper.java
    public static BorrowingDetailsDTO toDetailsDTO(Borrowing borrowing) {
        if (borrowing == null) {
            return null;
        }

        BorrowingDetailsDTO dto = new BorrowingDetailsDTO();
        dto.setId(borrowing.getId());

        // Map book information
        if (borrowing.getBook() != null) {
            dto.setBook(BookMapper.toBookDTO(borrowing.getBook()));
        }

        // Map member information
        if (borrowing.getMember() != null) {
            dto.setMember(MemberMapper.toDTO(borrowing.getMember()));
        }

        dto.setGuestName(borrowing.getGuestName());
        dto.setGuestEmail(borrowing.getGuestEmail());
        dto.setGuestPhone(borrowing.getGuestPhoneNumber());
        dto.setStatus(borrowing.getStatus());
        dto.setBorrowDate(DateTimeMapper.toString(borrowing.getBorrowedDate()));
        dto.setDueDate(DateTimeMapper.toString(borrowing.getDueDate()));
        dto.setReturnedDate(DateTimeMapper.toString(borrowing.getReturnedDate()));

        return dto;
    }

    public static Borrowing toEntity(BorrowingDTO borrowingDTO) {
        if (borrowingDTO == null) {
            return null;
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setId(borrowingDTO.getId());

        if (borrowingDTO.getMemberId() != null) {
            Member member = new Member();
            member.setId(borrowingDTO.getMemberId());
            borrowing.setMember(member);
        }

        if (borrowingDTO.getBookId() != null) {
            Book book = new Book();
            book.setId(borrowingDTO.getBookId());
            borrowing.setBook(book);
        }

        borrowing.setGuestName(borrowingDTO.getGuestName());
        borrowing.setGuestEmail(borrowingDTO.getGuestEmail());
        borrowing.setGuestPhoneNumber(borrowingDTO.getGuestPhone());
        borrowing.setStatus(borrowingDTO.getStatus());
        borrowing.setBorrowedDate(DateTimeMapper.toLocalDateTime(borrowingDTO.getBorrowDate()));
        borrowing.setDueDate(DateTimeMapper.toLocalDateTime(borrowingDTO.getDueDate()));
        borrowing.setReturnedDate(DateTimeMapper.toLocalDateTime(borrowingDTO.getReturnedDate()));

        return borrowing;
    }
}
