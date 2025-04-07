package com.ardnaxela.library_management_system.Borrowing;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.function.Supplier;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    Supplier<@org.jetbrains.annotations.NotNull Optional<? extends Borrowing>> findByGuestName(String guestName);

    Supplier<@org.jetbrains.annotations.NotNull Optional<? extends Borrowing>> findByMemberId(Long memberId);

    Optional<Object> findByGuestEmail(String guestEmail);

    @NotNull Optional<?> findByGuestPhoneNumber(String guestPhoneNumber);

    Optional<Borrowing> findByBookId(Long bookId);

    Optional<Borrowing> findByBookIdAndMemberId(Long bookId, Long memberId);

    Optional<Object> findByBookIdAndGuestName(Long bookId, String guestName);

    @NotNull Optional<? extends Borrowing> findFirstByGuestNameAndStatusOrderByBorrowedDateDesc(String guestName, String borrowed);

    @NotNull Optional<? extends Borrowing> findFirstByMemberIdAndStatusOrderByBorrowedDateDesc(Long memberId, String borrowed);
}
