package com.dactaphanmem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpVerificationRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    String username;

    @NotBlank(message = "Mã OTP không được để trống")
    String otp;
}
