package com.ardnaxela.library_management_system.Member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Member findByEmail(String email);

    Member findByPhoneNumber(String phoneNumber);
}
