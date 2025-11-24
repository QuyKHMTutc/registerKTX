package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "don_dang_ky", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ma_sv", "ma_dot"})
})
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sinhVien", "thoiGianDangKy"})
@ToString(exclude = {"sinhVien", "thoiGianDangKy"})
@EqualsAndHashCode(exclude = {"sinhVien", "thoiGianDangKy"})
public class DonDangKy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_don")
    private Integer maDon;

    @Column(name = "doi_tuong_uu_tien", length = 20)
    private String doiTuongUuTien;

    @Column(name = "ngay_gui")
    private LocalDateTime ngayGui;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ghi_chu")
    private String ghiChu;

    // --- QUAN HỆ ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", nullable = false) // Để JPA quản lý hoàn toàn
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dot", nullable = false) // Để JPA quản lý hoàn toàn
    private ThoiGianDangKy thoiGianDangKy;
}
