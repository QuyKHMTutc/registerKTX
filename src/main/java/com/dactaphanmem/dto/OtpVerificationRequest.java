package com.dactaphanmem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Mã OTP không được để trống")
    private String otp;
}
