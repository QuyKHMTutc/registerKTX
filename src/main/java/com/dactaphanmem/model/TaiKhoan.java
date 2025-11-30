package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tai_khoan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "sinhVien", "nhanVien" })
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tk")
    Integer maTK;

    @Column(name = "ten_dang_nhap", unique = true, nullable = false)
    String tenDangNhap;

    @Column(name = "mat_khau", nullable = false)
    String matKhau;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    boolean enabled = true; // Mặc định là true cho các tài khoản hiện có, sẽ thay đổi trong logic đăng ký
                            // mới

    @Column(name = "loai_tk", nullable = false, length = 2)
    String loaiTK; // "SV" hoặc "NV"

    @Column(name = "trang_thai", nullable = false)
    Integer trangThai; // 1 = active, 0 = inactive

    // Quan hệ OneToOne với SinhVien (nếu là sinh viên)
    @OneToOne(mappedBy = "taiKhoan", fetch = FetchType.LAZY)
    SinhVien sinhVien;

    // Quan hệ OneToOne với NhanVien (nếu là nhân viên)
    @OneToOne(mappedBy = "taiKhoan", fetch = FetchType.LAZY)
    NhanVien nhanVien;
}
