package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Mapper.MemberMapper;
import com.ardnaxela.library_management_system.Mapper.UserMapper;
import com.ardnaxela.library_management_system.Member.Member;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import com.ardnaxela.library_management_system.Member.MemberRepository;
import com.ardnaxela.library_management_system.Services.UserDetailsService;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserDTO;
import com.ardnaxela.library_management_system.User.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService, UserDetailsService {


    private final UserRepository userRepository;
    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword()) // Hashed password
                        .roles(user.getRole()) // Roles like USER or ADMIN
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public String createUser(UserDTO userDTO, MemberDTO memberDTO){
        // Check username if unique
        if(userRepository.existsByUsername(userDTO.getUsername())){
            return "Username already exists";
        }
        // Convert the UserDTO to User
        User user = UserMapper.toUser(userDTO);
        Member member = MemberMapper.toEntity(memberDTO);

        // Convert the MemberDTO to Member and encrypt the password
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        memberRepository.save(member);
        return "User registered successfully";

    }
}