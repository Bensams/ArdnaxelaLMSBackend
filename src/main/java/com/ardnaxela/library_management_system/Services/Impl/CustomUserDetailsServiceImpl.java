package com.ardnaxela.library_management_system.Services.Impl;

import com.ardnaxela.library_management_system.Components.JwtUtils;
import com.ardnaxela.library_management_system.Components.PasswordUtil;
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

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService, UserDetailsService {


    private final PasswordUtil passwordUtil;

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    private final JwtUtils jwtUtils;

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

    @Transactional
    @Override
    public String createUser(UserDTO userDTO, MemberDTO memberDTO){
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
}

