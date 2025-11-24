package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hop_dong")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sinhVien", "phong", "donDangKy"})
@ToString(exclude = {"sinhVien", "phong", "donDangKy"})
@EqualsAndHashCode(exclude = {"sinhVien", "phong", "donDangKy"})
public class HopDong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hd")
    private Integer maHD;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "gia_thuc_te", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal giaThucTe;

    @Column(name = "trang_thai")
    private String trangThai;

    // --- QUAN HỆ ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", nullable = false)
    private Phong phong;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don", nullable = false, unique = true)
    private DonDangKy donDangKy;
}
