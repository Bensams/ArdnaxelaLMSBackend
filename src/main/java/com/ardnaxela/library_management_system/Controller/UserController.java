package com.ardnaxela.library_management_system.Controller;

import com.ardnaxela.library_management_system.Borrowing.BorrowingDTO;
import com.ardnaxela.library_management_system.Components.JwtUtils;
import com.ardnaxela.library_management_system.DTO.BorrowingDetailsDTO;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import com.ardnaxela.library_management_system.Services.UserDetailsService;
import com.ardnaxela.library_management_system.User.UserDTO;
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

    @PutMapping("{id}")
    @PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable("id") Long userId, @RequestBody UserDTO userDTO) {
        // validate token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user has admin role
        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>("You don't have access to this", HttpStatus.FORBIDDEN);
        }
        // Your implementation
        String response = userDetailsService.updateUser( userId,userDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId) {
        // validate token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user has admin role
        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>("You don't have access to this", HttpStatus.FORBIDDEN);
        }
        // Your implementation
        String response = userDetailsService.deleteUser(userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // validate token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user has admin role
        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Your implementation
        List<UserDTO> userDTOs = userDetailsService.getAllUsers();

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
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
    @PostMapping("/borrow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> borrowBook(@RequestBody BorrowingDTO borrowingDTO) {
        // validate token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Your implementation
        userDetailsService.borrowBook(username, borrowingDTO);

        return new ResponseEntity<>("Book borrowed successfully", HttpStatus.OK);
    }

    @GetMapping("/borrowed-books")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BorrowingDetailsDTO>> getUserBorrowedBooks(
            @RequestParam(required = false) String status) {

        // validate token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<BorrowingDetailsDTO> borrowedBooks;

        if (status != null && !status.isEmpty()) {
            // Filter by status if provided
            borrowedBooks = userDetailsService.getBorrowingHistoryByUsernameAndStatus(username, status);
        } else {
            // Get all borrowings if no status filter
            borrowedBooks = userDetailsService.getBorrowingHistoryByUsername(username);
        }

        return new ResponseEntity<>(borrowedBooks, HttpStatus.OK);
    }

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