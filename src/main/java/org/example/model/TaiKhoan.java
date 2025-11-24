package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tai_khoan")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sinhVien", "nhanVien"})
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tk")
    private Integer maTK;

    @Column(name = "ten_dang_nhap", unique = true, nullable = false)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true; // Mặc định là true cho các tài khoản hiện có, sẽ thay đổi trong logic đăng ký mới

    @Column(name = "loai_tk", nullable = false, length = 2)
    private String loaiTK; // "SV" hoặc "NV"

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai; // 1 = active, 0 = inactive

    // Quan hệ OneToOne với SinhVien (nếu là sinh viên)
    @OneToOne(mappedBy = "taiKhoan", fetch = FetchType.LAZY)
    private SinhVien sinhVien;

    // Quan hệ OneToOne với NhanVien (nếu là nhân viên)
    @OneToOne(mappedBy = "taiKhoan", fetch = FetchType.LAZY)
    private NhanVien nhanVien;
}
