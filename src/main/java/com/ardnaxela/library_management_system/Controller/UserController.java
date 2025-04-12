package com.ardnaxela.library_management_system.Controller;

import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.Components.JwtUtils;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import com.ardnaxela.library_management_system.Services.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class UserController {
    private final UserDetailsService userDetailsService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberDTO> getUserProfile() {
        // validate token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Your implementation
        MemberDTO memberDTO = userDetailsService.getUserInfo(username);

        return new ResponseEntity<>(memberDTO, HttpStatus.OK);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberDTO> updateUserProfile(@RequestBody MemberDTO memberDTO) {
        // validate token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Your implementation
        MemberDTO updatedMemberDTO = userDetailsService.updateUserInfo(username, memberDTO);

        return new ResponseEntity<>(updatedMemberDTO, HttpStatus.OK);
    }

    // TODO: Implement borrow book method for user and validate token

//    @GetMapping("/{userId}/books")
//    @PreAuthorize("isAuthenticated() && #userId == principal.id")
//    public ResponseEntity<List<BorrowingDTO>> getUserBooks(
//            @PathVariable String token,
//            @RequestParam(required = false) String status) {
//
////        BorrowingDTO borrowings = userDetailsService.getUserInfo(token);
//
//
//    }
}