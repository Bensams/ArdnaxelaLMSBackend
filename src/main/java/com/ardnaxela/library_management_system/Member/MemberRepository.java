package com.ardnaxela.library_management_system.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Member findByEmail(String email);

    Member findByPhoneNumber(String phoneNumber);

    @Query("SELECT m FROM Member m WHERE m.user.username = :username")
    Member findByUsername(@Param("username") String username);

    Optional<Member> findByUserId(Long userId);
}
