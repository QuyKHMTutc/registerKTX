package com.dactaphanmem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for student dormitory application form.
 * Receives data from student HTML form submission.
 */
@Data
public class DonDangKyForm {

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String maSV; // Mã Sinh Viên nộp đơn

    @NotNull(message = "Mã đợt đăng ký không được để trống")
    private Integer maDot; // Mã Đợt Đăng Ký

    @NotBlank(message = "Đối tượng ưu tiên không được để trống")
    private String doiTuongUuTien; // Loại ưu tiên được chọn

    private String ghiChu; // Ghi chú thêm từ sinh viên (optional)

    // Yêu cầu chọn phòng
    @NotNull(message = "Mã loại phòng không được để trống")
    private Integer maLoaiPhong;

    private Integer maPhongYeuCau; // Mã phòng sinh viên yêu cầu (optional)
}
