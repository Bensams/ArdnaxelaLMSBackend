// SecurityConfig.java
package com.ardnaxela.library_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/api/books/**").permitAll()
//                .anyRequest().authenticated()
//            )
//            .cors(withDefaults()); // Enable CORS with default configuration
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF (optional)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Allow all requests without auth
                )
                // Disable HTTPS requirement (remove requiresSecure())
                .requiresChannel(channel ->
                        channel.anyRequest().requiresInsecure()  // Allow HTTP
                );

        return http.build();
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