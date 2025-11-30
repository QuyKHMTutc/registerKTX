package com.dactaphanmem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO for student dormitory application form.
 * Receives data from student HTML form submission.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonDangKyForm {

    @NotBlank(message = "Mã sinh viên không được để trống")
    String maSV; // Mã Sinh Viên nộp đơn

    @NotNull(message = "Mã đợt đăng ký không được để trống")
    Integer maDot; // Mã Đợt Đăng Ký

    @NotBlank(message = "Đối tượng ưu tiên không được để trống")
    String doiTuongUuTien; // Loại ưu tiên được chọn

    String ghiChu; // Ghi chú thêm từ sinh viên (optional)

    // Yêu cầu chọn phòng
    @NotNull(message = "Mã loại phòng không được để trống")
    Integer maLoaiPhong;

    Integer maPhongYeuCau; // Mã phòng sinh viên yêu cầu (optional)
}
