// SecurityConfig.java
package com.ardnaxela.library_management_system.config;

import com.ardnaxela.library_management_system.Services.Impl.CustomUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())  // Disable CSRF (optional)
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()  // Allow all requests without auth
//                )
//                // Disable HTTPS requirement (remove requiresSecure())
//                .requiresChannel(channel ->
//                        channel.anyRequest().requiresInsecure()  // Allow HTTP
//                );
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/login", "/signup").permitAll()  // Allow these without authentication
                .anyRequest().authenticated()
                .and()
                .formLogin().disable(); // Disable Spring's default login form

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder, CustomUserDetailsServiceImpl userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
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
        configuration.setAllowedOriginPatterns(List.of(
                "https://4173-idx-ardnaxelalms-1743925645039.cluster-a3grjzek65cxex762e4mwrzl46.cloudworkstations.dev/books" // Allows all origins
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
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}