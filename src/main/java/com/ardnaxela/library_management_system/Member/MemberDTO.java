package com.ardnaxela.library_management_system.Member;


import com.ardnaxela.library_management_system.User.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String username; // Assuming User is a class that contains user-related information
    private String createdAt;
}
