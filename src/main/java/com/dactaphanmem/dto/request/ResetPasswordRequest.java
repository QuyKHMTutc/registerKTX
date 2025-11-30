package com.dactaphanmem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống.")
    String username;

    @NotBlank(message = "Mã OTP không được để trống.")
    @Pattern(regexp = "[0-9]{6}", message = "Mã OTP phải là 6 chữ số.")
    String otp;

    @NotBlank(message = "Mật khẩu mới không được để trống.")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự.")
    String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống.")
    String confirmPassword;
}
