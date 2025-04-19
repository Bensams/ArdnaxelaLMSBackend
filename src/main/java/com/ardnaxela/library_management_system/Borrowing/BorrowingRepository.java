package com.ardnaxela.library_management_system.Borrowing;

import com.ardnaxela.library_management_system.Member.Member;
import com.ardnaxela.library_management_system.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    List<Borrowing> findByGuestNameContainingIgnoreCaseAndStatusNot(String name, String status);
    List<Borrowing> findByGuestEmailContainingIgnoreCaseAndStatusNot(String email, String status);
    List<Borrowing> findByGuestPhoneNumberContainingAndStatusNot(String phone, String status);

    Optional<Borrowing> findByBookIdAndMemberId(Long bookId, Long memberId);

    Optional<Object> findByBookIdAndGuestName(Long bookId, String guestName);

    // Add these methods to BorrowingRepository.java
    @Query("SELECT b FROM Borrowing b WHERE b.member.user.username = :username ORDER BY b.borrowedDate DESC")
    List<Borrowing> findByMemberUsername(@Param("username") String username);

    @Query("SELECT b FROM Borrowing b WHERE b.member.user.username = :username AND b.status = :status ORDER BY b.borrowedDate DESC")
    List<Borrowing> findByMemberUsernameAndStatus(@Param("username") String username, @Param("status") String status);

    List<Borrowing> findByMemberId(Long memberId);

    List<Borrowing> findByMemberIdAndStatus(Long memberId, String status);

    List<Borrowing> findByStatusAndDueDateBefore(String borrowed, LocalDateTime now);

    Optional<Borrowing> findByBookId(Long id);

    List<Borrowing> findByStatus(String status);

    @Query("SELECT b FROM Borrowing b WHERE b.status = 'BORROWED' AND b.dueDate BETWEEN :start AND :end")
    List<Borrowing> findByStatusAndDueDateBetween(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);
}
