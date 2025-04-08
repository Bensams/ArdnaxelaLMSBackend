package com.ardnaxela.library_management_system.Components;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {
    private final PasswordEncoder pwEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String password) {
        return pwEncoder.encode(password);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return pwEncoder.matches(rawPassword, encodedPassword);
    }
}
