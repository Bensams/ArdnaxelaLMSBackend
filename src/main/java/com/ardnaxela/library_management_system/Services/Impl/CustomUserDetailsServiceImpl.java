package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Borrowing.Borrowing;
import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.Borrowing.BorrowingRepository;
import com.ardnaxela.library_management_system.Components.JwtUtils;
import com.ardnaxela.library_management_system.Components.PasswordUtil;
import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Mapper.BorrowingMapper;
import com.ardnaxela.library_management_system.Services.BorrowingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.ardnaxela.library_management_system.Mapper.MemberMapper;
import com.ardnaxela.library_management_system.Mapper.UserMapper;
import com.ardnaxela.library_management_system.Member.Member;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import com.ardnaxela.library_management_system.Member.MemberRepository;
import com.ardnaxela.library_management_system.Services.UserDetailsService;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserDTO;
import com.ardnaxela.library_management_system.User.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService, UserDetailsService {


    private final PasswordUtil passwordUtil;

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final BorrowingService borrowingService;
    private final JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        // Ensure roles are loaded here
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public String createUser(UserDTO userDTO, MemberDTO memberDTO ){
        // Check if username is unique
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            return "Username already exists";
        }


        // Convert the DTOs to entities
        User user = UserMapper.toUser(userDTO);
        Member member = MemberMapper.toEntity(memberDTO);

        // Hash the password and save the User
        user.setPassword(passwordUtil.hashPassword(user.getPassword()));

        userRepository.save(user);

        member.setUser(user); // Set the User reference to the Member
        // Save the Member first
        memberRepository.save(member);

        user.setMember(member);
        userRepository.save(user);

        return "User registered successfully";

    }

    @Override
    public String addUser(UserDTO userDTO) {
        User user = UserMapper.toUser(userDTO);
        Member member = MemberMapper.toEntity(userDTO.getMemberDTO());

        user.setPassword(passwordUtil.hashPassword(user.getPassword()));

        userRepository.save(user);
        member.setUser(user); // Set the User reference to the Member
        memberRepository.save(member);
        user.setMember(member);
        userRepository.save(user);
        return "User registered successfully from management";
    }

    @Override
    public String updateUser(Long userID, UserDTO userDTO) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setRole(userDTO.getRole());

        Member member = memberRepository.findById(user.getMember().getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        // Update the member information
        member.setName(userDTO.getMemberDTO().getName());
        member.setEmail(userDTO.getMemberDTO().getEmail());
        member.setPhoneNumber(userDTO.getMemberDTO().getPhoneNumber());
        member.setAddress(userDTO.getMemberDTO().getAddress());
        // Save the updated member
        memberRepository.save(member);

        userRepository.save(user);

        return "User updated successfully";
    }

    @Override
    public String deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Member member = memberRepository.findById(user.getMember().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Member not found"));

            // Delete the member first
            memberRepository.delete(member);
            // Then delete the user
            userRepository.delete(user);

            return "User deleted successfully";
        } catch (EntityNotFoundException e) {
            return "User not found";
        }

    }

    @Override
    public MemberDTO getUserInfo(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Member member = memberRepository.findById(user.getMember().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        return MemberMapper.toDTO(member);
    }

    @Override
    public MemberDTO updateUserInfo(String username, MemberDTO memberDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Member member = memberRepository.findById(user.getMember().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        // Update the member information
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        member.setPhoneNumber(memberDTO.getPhoneNumber());
        member.setAddress(memberDTO.getAddress());

        // Save the updated member
        memberRepository.save(member);
        // Return the updated member information
        return MemberMapper.toDTO(member);
    }

    @Override
    public void borrowBook(String username, BorrowingDTO borrowingDTO) {
        borrowingDTO.setMemberId(memberRepository.findByUsername(username).getId());
        borrowingService.borrowBook(borrowingDTO);
    }

    @Override
    public List<BorrowingDetailsDTO> getBorrowingHistoryByUsername(String username) {
        // Get all borrowings for this member
        return borrowingService.getBorrowingHistoryByUsername(username);

    }

    @Override
    public List<BorrowingDetailsDTO> getBorrowingHistoryByUsernameAndStatus(String username, String status) {

        // Get borrowings filtered by status
        return borrowingService.getBorrowingHistoryByUsernameAndStatus(username, status);

    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }
}

