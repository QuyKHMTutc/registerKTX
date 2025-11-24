package com.dactaphanmem.service;

import com.dactaphanmem.dto.RegisterRequest;
import com.dactaphanmem.model.TaiKhoan;
import com.dactaphanmem.model.SinhVien;
import com.dactaphanmem.repository.NhanVienRepository;
import com.dactaphanmem.repository.SinhVienRepository;
import com.dactaphanmem.repository.TaiKhoanRepository;
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
    private NhanVienRepository nhanVienRepository;
    @Mock
    private SinhVienRepository sinhVienRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        // Mock data
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setMatKhau(password);
        registerRequest.setEmail(email);

        when(taiKhoanRepository.existsByTenDangNhap(username)).thenReturn(false);
        when(taiKhoanRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(taiKhoanRepository.save(any(TaiKhoan.class))).thenAnswer(invocation -> {
            TaiKhoan taiKhoan = invocation.getArgument(0);
            taiKhoan.setMaTK(1); // Simulate ID generation
            return taiKhoan;
        });
        when(sinhVienRepository.save(any(SinhVien.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());


        // Execute
        TaiKhoan result = authenticationService.register(registerRequest);

        // Verify
        assertNotNull(result);
        assertEquals(username, result.getTenDangNhap());
        assertEquals(email, result.getEmail());
        assertEquals("encodedPassword", result.getMatKhau());
        assertFalse(result.isEnabled()); // Account should be disabled until activated
        assertEquals("SV", result.getLoaiTK());
        assertEquals(1, result.getTrangThai());

        verify(taiKhoanRepository).existsByTenDangNhap(username);
        verify(taiKhoanRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(taiKhoanRepository).save(any(TaiKhoan.class));
        verify(sinhVienRepository).save(any(SinhVien.class));
        verify(emailService).sendSimpleMessage(eq(email), anyString(), anyString());
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        String username = "existinguser";
        String password = "password";
        String email = "test@example.com";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setMatKhau(password);
        registerRequest.setEmail(email);

        when(taiKhoanRepository.existsByTenDangNhap(username)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(registerRequest);
        });

        assertEquals("Tên đăng nhập đã tồn tại.", exception.getMessage());
        verify(taiKhoanRepository).existsByTenDangNhap(username);
        verifyNoMoreInteractions(taiKhoanRepository, passwordEncoder, sinhVienRepository, emailService);
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        String username = "testuser";
        String password = "password";
        String email = "existing@example.com";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setMatKhau(password);
        registerRequest.setEmail(email);

        when(taiKhoanRepository.existsByTenDangNhap(username)).thenReturn(false);
        when(taiKhoanRepository.existsByEmail(email)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(registerRequest);
        });

        assertEquals("Email đã được sử dụng.", exception.getMessage());
        verify(taiKhoanRepository).existsByTenDangNhap(username);
        verify(taiKhoanRepository).existsByEmail(email);
        verifyNoMoreInteractions(taiKhoanRepository, passwordEncoder, sinhVienRepository, emailService);
    }
}
