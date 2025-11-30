package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hop_dong")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HopDong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hd")
    Integer maHD;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don", nullable = false, unique = true)
    DonDangKy donDangKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", nullable = false)
    SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", nullable = false)
    Phong phong;

    @Column(name = "ngay_bat_dau")
    LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    LocalDate ngayKetThuc;

    @Column(name = "gia_thuc_te")
    BigDecimal giaThucTe;

    @Column(name = "trang_thai")
    String trangThai;
}
