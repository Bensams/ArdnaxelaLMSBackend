package com.ardnaxela.library_management_system.Services;

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
}
