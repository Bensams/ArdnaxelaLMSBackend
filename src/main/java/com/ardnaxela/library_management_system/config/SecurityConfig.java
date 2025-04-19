// SecurityConfig.java
package com.ardnaxela.library_management_system.config;

import com.ardnaxela.library_management_system.Components.JwtAuthenticationFilter;
import com.ardnaxela.library_management_system.Components.PasswordUtil;
import com.ardnaxela.library_management_system.Services.Impl.CustomUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection if not required
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("auth/**", "/api/**").permitAll() // Allow these URLs without authentication
//                        .anyRequest().authenticated() // All other requests require authentication
//                )
//                .formLogin(form -> form.disable()) // Disable Spring's default login form
//                .cors(cors -> {}); // Apply CORS configuration defined in CorsConfigurationSource bean
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("auth/**", "api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .cors(cors -> {});

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder
    ) throws Exception {
        var authManagerBuilder = new AuthenticationManagerBuilder(new ObjectPostProcessor<Object>() {
            @Override
            public <O> O postProcess(O object) {
                return object; // No special post-processing
            }
        });

        authManagerBuilder
                .userDetailsService(userDetailsService) // Your custom UserDetailsService
                .passwordEncoder(passwordEncoder);       // Your PasswordEncoder (e.g., BCrypt)

        return authManagerBuilder.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/api/books/**",  // Your API endpoint
                "/error"          // Error endpoint
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use allowedOriginPatterns instead of allowedOrigins with wildcard
        configuration.setAllowedOriginPatterns(List.of(frontendUrl,
                "https://9000-idx-ardnaxelalms-1743925645039.cluster-a3grjzek65cxex762e4mwrzl46.cloudworkstations.dev",
                "https://4173-idx-ardnaxelalms-1743925645039.cluster-a3grjzek65cxex762e4mwrzl46.cloudworkstations.dev/"// Allows all origins
//                "https://4173-idx-ardnaxelalms-1743925645039.cluster-a3grjzek65cxex762e4mwrzl46.cloudworkstations.dev", //
//                "https://5173-idx-ardnaxelalms-1743925645039.cluster-a3grjzek65cxex762e4mwrzl46.cloudworkstations.dev/",
//                "https://8443-idx-ardnaxelalms-1743925645039.cluster-a3grjzek65cxex762e4mwrzl46.cloudworkstations.dev/api/books",
//                "https://4173-idx-ardnaxelalms-1743925645039.cluster-a3grjzek65cxex762e4mwrzl46.cloudworkstations.dev/books"
//                "http://localhost:[*]", // Allows any port from localhost
//                "http://127.0.0.1:[*]", // Also allow 127.0.0.1
//                "https://localhost:[*]",
//                "https://127.0.0.1:[*]"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}