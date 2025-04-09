package com.ardnaxela.library_management_system.Components;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtil {
    private final PasswordEncoder pwEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String password) {
        return pwEncoder.encode(password);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return pwEncoder.matches(rawPassword, encodedPassword);
    }
}
