package com.ardnaxela.library_management_system.Services;

import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.DTO.LoginRequestDTO;
import com.ardnaxela.library_management_system.Member.Member;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import com.ardnaxela.library_management_system.User.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserDetailsService {

    public UserDetails loadUserByUsername(String username);
    public String createUser(UserDTO userDTO, MemberDTO memberDTO);

    public MemberDTO getUserInfo(String username);

    public MemberDTO updateUserInfo(String username, MemberDTO memberDTO);

    void borrowBook(String username, BorrowingDTO borrowingDTO);

    List<BorrowingDetailsDTO> getBorrowingHistoryByUsername(String username);

    List<BorrowingDetailsDTO> getBorrowingHistoryByUsernameAndStatus(String username, String status);

    List<UserDTO> getAllUsers();

    String addUser(UserDTO userDTO);

    String updateUser(Long userID, UserDTO userDTO);

    String deleteUser(Long userId);
}
