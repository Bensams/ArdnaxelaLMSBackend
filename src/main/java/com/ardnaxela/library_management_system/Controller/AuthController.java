package com.ardnaxela.library_management_system.Controller;

import com.ardnaxela.library_management_system.Components.JwtUtils;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import com.ardnaxela.library_management_system.Services.UserDetailsService;
import com.ardnaxela.library_management_system.User.User;
import com.ardnaxela.library_management_system.User.UserDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils; // The utility class to generate tokens

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        // Authenticate user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

        return jwtUtils.generateToken(loginRequest.getUsername());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupDTO signupDTO) {
        // Convert the signupDTO to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(signupDTO.getUsername());
        userDTO.setPassword(signupDTO.getPassword());
        userDTO.setRole(signupDTO.getRole());
        // Convert the signupDTO to MemberDTO
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName(signupDTO.getName());
        memberDTO.setEmail(signupDTO.getEmail());
        // Hash the password and save the user
        return userDetailsService.createUser(userDTO, memberDTO);
    }
}

@Data
class SignupDTO {
    private String name;
    private String email;
    private String username;
    private String password;
    private String role;
}

@Getter
@Setter
class LoginRequest {
    private String username;
    private String password;
    // Getters and Setters
}