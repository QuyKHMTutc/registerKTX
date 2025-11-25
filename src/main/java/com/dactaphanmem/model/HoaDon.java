package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate; // Changed back to LocalDate

@Entity
@Table(name = "hoa_don")
@Data
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hoa_don")
    private Integer maHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hop_dong", nullable = false)
    private HopDong hopDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", nullable = false)
    private SinhVien sinhVien;

    @Column(name = "tieu_de")
    private String tieuDe;

    @Column(name = "loai_hoa_don")
    private String loaiHoaDon;

    @Column(name = "thang_nam")
    private String thangNam;

    @Column(name = "so_tien")
    private BigDecimal soTien;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao; // Changed back to LocalDate

    @Column(name = "han_thanh_toan")
    private LocalDate hanThanhToan; // Changed back to LocalDate

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ma_nv_tao")
    private Integer maNVTao;
}
