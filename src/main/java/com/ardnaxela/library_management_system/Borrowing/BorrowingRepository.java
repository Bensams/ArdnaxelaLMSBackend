package com.ardnaxela.library_management_system.Borrowing;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    Optional<List<Borrowing>> findByGuestName(String guestName);

    Optional<List<Borrowing>> findByGuestEmail(String guestEmail);

    Optional<List<Borrowing>> findByGuestPhoneNumber(String guestPhone);

    Optional<Borrowing> findByBookIdAndMemberId(Long bookId, Long memberId);

    Optional<Object> findByBookIdAndGuestName(Long bookId, String guestName);
}
