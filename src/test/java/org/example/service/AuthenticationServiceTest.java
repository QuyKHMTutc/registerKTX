package org.example.service;

import org.example.model.TaiKhoan;
import org.example.repository.TaiKhoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private TaiKhoanRepository taiKhoanRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Mock data
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";

        when(taiKhoanRepository.findByTenDangNhap(username)).thenReturn(Optional.empty());
        when(taiKhoanRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(taiKhoanRepository.save(any(TaiKhoan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        TaiKhoan result = authenticationService.registerUser(username, password, email);

        // Verify
        assertNotNull(result);
        assertEquals(username, result.getTenDangNhap());
        assertEquals(email, result.getEmail());
        assertEquals("encodedPassword", result.getMatKhau());
        verify(taiKhoanRepository).save(any(TaiKhoan.class));
    }
}
