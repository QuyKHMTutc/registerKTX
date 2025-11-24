package com.dactaphanmem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private AuthenticationSuccessHandler authenticationSuccessHandler;

        @Autowired
        private AuthenticationFailureHandler customAuthenticationFailureHandler; // Inject handler mới

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // 1. Cho phép truy cập công khai
                                                .requestMatchers("/auth/**", "/css/**", "/js/**", "/images/**",
                                                                "/webjars/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // 2. Các tuyến đường của Admin (cả web và API)
                                                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                                                // 3. Các tuyến đường của User (cả web và API)
                                                .requestMatchers("/user/**", "/api/user/**").hasRole("SINH_VIEN")

                                                // 4. Tất cả các request khác cần đăng nhập
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/auth/login")
                                                .loginProcessingUrl("/auth/login")
                                                .usernameParameter("tenDangNhap")
                                                .passwordParameter("matKhau")
                                                .successHandler(authenticationSuccessHandler)
                                                .failureHandler(customAuthenticationFailureHandler) // Sử dụng handler
                                                                                                    // của chúng ta
                                                .permitAll())
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/auth/login?accessDenied=true"))
                                .logout(logout -> logout
                                                .logoutUrl("/auth/logout")
                                                .logoutSuccessUrl("/auth/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .sessionManagement(session -> session
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false))
                                // Security Headers - Protect against common web vulnerabilities
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.deny()) // Prevent clickjacking
                                                .contentTypeOptions(contentType -> contentType.disable()) // X-Content-Type-Options:
                                                                                                          // nosniff
                                                .xssProtection(xss -> xss.disable()) // Modern browsers have built-in
                                                                                     // XSS protection
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives("default-src 'self'; " +
                                                                                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://code.jquery.com; "
                                                                                +
                                                                                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; "
                                                                                +
                                                                                "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; "
                                                                                +
                                                                                "img-src 'self' data: https:;")));

                return http.build();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder);
        }
}
