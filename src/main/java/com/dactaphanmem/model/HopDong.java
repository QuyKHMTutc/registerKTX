package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate; // Changed back to LocalDate

@Entity
@Table(name = "hop_dong")
@Data
public class HopDong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hd")
    private Integer maHD;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don", nullable = false, unique = true)
    private DonDangKy donDangKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", nullable = false)
    private Phong phong;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau; // Changed back to LocalDate

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc; // Changed back to LocalDate

    @Column(name = "gia_thuc_te")
    private BigDecimal giaThucTe;

    @Column(name = "trang_thai")
    private String trangThai;
}
