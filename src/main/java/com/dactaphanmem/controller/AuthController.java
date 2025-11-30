package com.dactaphanmem.controller;

import com.dactaphanmem.dto.request.*;
import com.dactaphanmem.dto.response.ApiResponse;
import com.dactaphanmem.dto.response.JwtAuthResponse;
import com.dactaphanmem.security.JwtTokenProvider;
import com.dactaphanmem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", JwtAuthResponse.builder()
                .accessToken(jwtTokenProvider.generateToken(auth))
                .username(req.getUsername())
                .role(auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_USER"))
                .build()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest req) {
        if (!req.getMatKhau().equals(req.getXacNhanMatKhau())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Mật khẩu xác nhận không khớp"));
        }
        authService.register(req);
        return ResponseEntity
                .ok(ApiResponse.success("Đăng ký thành công! Vui lòng kiểm tra email để nhận mã OTP.", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody OtpVerificationRequest req) {
        authService.verifyOtpAndActivateAccount(req.getUsername(), req.getOtp());
        return ResponseEntity.ok(ApiResponse.success("Xác thực tài khoản thành công!", null));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<String>> resendOtp(@RequestParam String username) {
        authService.resendActivationOtp(username);
        return ResponseEntity.ok(ApiResponse.success("Một mã OTP mới đã được gửi đến email của bạn.", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.generatePasswordResetToken(req.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Mã OTP đã được gửi đến email của bạn.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Mật khẩu mới và xác nhận mật khẩu không khớp"));
        }
        authService.resetPassword(req.getUsername(), req.getOtp(), req.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Mật khẩu đã được đặt lại thành công!", null));
    }
}
