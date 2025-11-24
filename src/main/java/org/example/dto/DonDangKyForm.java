package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

// Đối tượng này nhận dữ liệu từ form HTML của sinh viên
@Data
public class DonDangKyForm {
    private String maSV; // Mã Sinh Viên nộp đơn
    private Integer maDot; // Mã Đợt Đăng Ký
    private String doiTuongUuTien; // Loại ưu tiên được chọn
    private String ghiChu; // Ghi chú thêm từ sinh viên

    // Yêu cầu chọn phòng
    private Integer maLoaiPhong;
    private Integer maPhongYeuCau; // Mã phòng sinh viên yêu cầu (Có thể là NULL)
}
