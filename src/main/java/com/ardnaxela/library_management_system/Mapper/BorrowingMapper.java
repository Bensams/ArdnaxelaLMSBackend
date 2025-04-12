package com.ardnaxela.library_management_system.Mapper;

import com.ardnaxela.library_management_system.Book.Book;
import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.Member.Member;

public class BorrowingMapper {

    // Add your mapping methods here
    // For example, you can create a method to convert Borrowing entity to BorrowingDTO
    // and vice versa.

    // Example:
    // public BorrowingDTO toDto(Borrowing borrowing) {
    //     if (borrowing == null) {
    //         return null;
    //     }
    //     BorrowingDTO dto = new BorrowingDTO();
    //     dto.setId(borrowing.getId());
    //     dto.setMemberId(borrowing.getMember().getId());
    //     dto.setBookId(borrowing.getBook().getId());
    //     dto.setBorrowDate(borrowing.getBorrowDate());
    //     dto.setReturnDate(borrowing.getReturnDate());
    //     return dto;
    // }
//    private Long id;
//    private Long bookId;
//    private Long memberId;
//    private String guestName; // For guest users
//    private String guestEmail; // For guest users
//    private String guestPhoneNumber; // For guest users
//    private String status; // e.g., "BORROWED", "RETURNED", "OVERDUE", PENDING, CANCELLED
//    private String borrowDate;
//    private String dueDate;
//    private String returnedDate;

    public static BorrowingDTO toDTO(Borrowing borrowing) {
        if (borrowing == null) {
            return null;
        }
        BorrowingDTO borrowingDTO = new BorrowingDTO();
        borrowingDTO.setId(borrowing.getId());
        // Check if member is null before accessing it
        if (borrowing.getMember() != null) {
            borrowingDTO.setMemberId(borrowing.getMember().getId());
        } else {
            borrowingDTO.setMemberId(null); // Set null or any default value
        }

        borrowingDTO.setBookId(borrowing.getBook().getId());
        borrowingDTO.setGuestName(borrowing.getGuestName());
        borrowingDTO.setGuestEmail(borrowing.getGuestEmail());
        borrowingDTO.setGuestPhone(borrowing.getGuestPhoneNumber());
        borrowingDTO.setStatus(borrowing.getStatus());
        borrowingDTO.setBorrowDate(DateTimeMapper.toString(borrowing.getBorrowedDate()));
        borrowingDTO.setDueDate(DateTimeMapper.toString(borrowing.getDueDate()));
        borrowingDTO.setReturnedDate(DateTimeMapper.toString(borrowing.getReturnedDate()));

        return borrowingDTO;
    }

    public static Borrowing toEntity(BorrowingDTO borrowingDTO) {
        if (borrowingDTO == null) {
            return null;
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setId(borrowingDTO.getId());
        if (borrowingDTO.getMemberId() != null) { // Check if memberId is not null
            Member member = new Member();
            member.setId(borrowingDTO.getMemberId());
            borrowing.setMember(member);
        }
        if (borrowingDTO.getBookId() != null) { // Check if bookId is not null
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
