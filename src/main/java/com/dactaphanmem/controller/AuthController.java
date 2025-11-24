package com.dactaphanmem.controller;

import jakarta.validation.Valid;
import com.dactaphanmem.dto.ForgotPasswordRequest;
import com.dactaphanmem.dto.OtpVerificationRequest;
import com.dactaphanmem.dto.RegisterRequest;
import com.dactaphanmem.dto.ResetPasswordRequest;
import com.dactaphanmem.model.TaiKhoan; // Import TaiKhoan
import com.dactaphanmem.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đăng ký.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest",
                    bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/auth/register";
        }

        // So sánh mật khẩu an toàn (tránh NullPointerException)
        String matKhau = registerRequest.getMatKhau();
        String xacNhanMatKhau = registerRequest.getXacNhanMatKhau();
        if (matKhau == null || xacNhanMatKhau == null || !matKhau.equals(xacNhanMatKhau)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/auth/register";
        }

        try {
            authService.register(registerRequest);
            return "redirect:/auth/verify?username=" + registerRequest.getUsername() + "&source=register";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/auth/register";
        } catch (Exception e) {
            logger.error("Lỗi khi đăng ký tài khoản: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi không mong muốn xảy ra khi đăng ký.");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/verify")
    public String showVerifyPage(@RequestParam String username,
            @RequestParam(required = false) String source,
            Model model) {
        OtpVerificationRequest request = new OtpVerificationRequest();
        request.setUsername(username);
        model.addAttribute("otpVerificationRequest", request);

        if ("register".equals(source)) {
            model.addAttribute("infoMessage",
                    "Đăng ký thành công! Vui lòng kiểm tra email để nhận mã OTP và hoàn tất xác thực.");
        }

        return "auth/verify-otp";
    }

    @PostMapping("/verify")
    public String handleVerify(@Valid @ModelAttribute("otpVerificationRequest") OtpVerificationRequest otpRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập mã OTP.");
            return "redirect:/auth/verify?username=" + otpRequest.getUsername();
        }

        try {
            authService.verifyOtpAndActivateAccount(otpRequest.getUsername(), otpRequest.getOtp());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Xác thực tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/verify?username=" + otpRequest.getUsername();
        } catch (Exception e) {
            logger.error("Lỗi khi xác thực OTP: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi không mong muốn xảy ra khi xác thực OTP.");
            return "redirect:/auth/verify?username=" + otpRequest.getUsername();
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String username, RedirectAttributes redirectAttributes) {
        try {
            authService.resendActivationOtp(username);
            redirectAttributes.addFlashAttribute("successMessage", "Một mã OTP mới đã được gửi đến email của bạn.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi khi gửi lại OTP cho {}: {}", username, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cố gắng gửi lại mã OTP.");
        }
        return "redirect:/auth/verify?username=" + username;
    }

    // === QUÊN MẬT KHẨU ===

    /**
     * Hiển thị form yêu cầu đặt lại mật khẩu (nhập email).
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }

    /**
     * Xử lý yêu cầu quên mật khẩu: gửi OTP đến email.
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập email hợp lệ.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.forgotPasswordRequest",
                    bindingResult);
            redirectAttributes.addFlashAttribute("forgotPasswordRequest", request);
            return "redirect:/auth/forgot-password";
        }

        try {
            authService.generatePasswordResetToken(request.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra email để đặt lại mật khẩu.");
            // Chuyển hướng đến trang đặt lại mật khẩu, truyền email để điền sẵn (URL
            // encoded)
            String encodedEmail = URLEncoder.encode(request.getEmail(), StandardCharsets.UTF_8);
            return "redirect:/auth/reset-password?email=" + encodedEmail;
        } catch (IllegalArgumentException e) {
            // Không tiết lộ email có tồn tại hay không vì lý do bảo mật
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nếu email của bạn tồn tại trong hệ thống, một mã OTP đã được gửi.");
            return "redirect:/auth/forgot-password";
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý quên mật khẩu cho email {}: {}", request.getEmail(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xử lý yêu cầu quên mật khẩu.");
            return "redirect:/auth/forgot-password";
        }
    }

    /**
     * Hiển thị form đặt lại mật khẩu (nhập OTP và mật khẩu mới).
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(required = false) String email, Model model) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        // Cố gắng tìm username từ email để điền sẵn vào form
        if (email != null && !email.isBlank()) {
            authService.getTaiKhoanByEmail(email).ifPresent(taiKhoan -> request.setUsername(taiKhoan.getTenDangNhap()));
        }
        model.addAttribute("resetPasswordRequest", request);
        return "auth/reset-password";
    }

    /**
     * Xử lý việc đặt lại mật khẩu.
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        // Helper để lấy email đã encode
        String encodedEmail = "";
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            encodedEmail = authService.getTaiKhoanByUsername(request.getUsername())
                    .map(TaiKhoan::getEmail)
                    .map(email -> URLEncoder.encode(email, StandardCharsets.UTF_8))
                    .orElse("");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.resetPasswordRequest",
                    bindingResult);
            redirectAttributes.addFlashAttribute("resetPasswordRequest", request);
            return "redirect:/auth/reset-password?email=" + encodedEmail;
        }

        // So sánh mật khẩu an toàn (tránh NullPointerException)
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();
        if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            redirectAttributes.addFlashAttribute("resetPasswordRequest", request);
            return "redirect:/auth/reset-password?email=" + encodedEmail;
        }

        try {
            authService.resetPassword(request.getUsername(), request.getOtp(), request.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Mật khẩu đã được đặt lại thành công! Bạn có thể đăng nhập.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("resetPasswordRequest", request);
            return "redirect:/auth/reset-password?email=" + encodedEmail;
        } catch (Exception e) {
            logger.error("Lỗi khi đặt lại mật khẩu cho tài khoản {}: {}", request.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi đặt lại mật khẩu.");
            redirectAttributes.addFlashAttribute("resetPasswordRequest", request);
            return "redirect:/auth/reset-password?email=" + encodedEmail;
        }
    }
}
