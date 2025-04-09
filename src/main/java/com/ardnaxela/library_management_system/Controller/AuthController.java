package com.ardnaxela.library_management_system.Controller;

import com.ardnaxela.library_management_system.Components.JwtUtils;
import com.ardnaxela.library_management_system.DTO.LoginRequestDTO;
import com.ardnaxela.library_management_system.Member.MemberDTO;
import com.ardnaxela.library_management_system.Services.UserDetailsService;
import com.ardnaxela.library_management_system.User.UserDTO;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final UserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils; // The utility class to generate tokens


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String jwt = jwtUtils.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
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

@Data
class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}