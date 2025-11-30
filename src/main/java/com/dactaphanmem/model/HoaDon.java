package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hoa_don")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hoa_don")
    Integer maHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hop_dong", nullable = false)
    HopDong hopDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", nullable = false)
    SinhVien sinhVien;

    @Column(name = "tieu_de")
    String tieuDe;

    @Column(name = "loai_hoa_don")
    String loaiHoaDon;

    @Column(name = "thang_nam")
    String thangNam;

    @Column(name = "so_tien")
    BigDecimal soTien;

    @Column(name = "ngay_tao")
    LocalDate ngayTao;

    @Column(name = "han_thanh_toan")
    LocalDate hanThanhToan;

    @Column(name = "trang_thai")
    String trangThai;

    @Column(name = "ma_nv_tao")
    Integer maNVTao;
}
