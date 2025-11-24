package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hoa_don")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sinhVien", "hopDong", "nhanVien"})
@ToString(exclude = {"sinhVien", "hopDong", "nhanVien"})
@EqualsAndHashCode(exclude = {"sinhVien", "hopDong", "nhanVien"})
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hoa_don")
    private Integer maHoaDon;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "loai_hoa_don", nullable = false, length = 20)
    private String loaiHoaDon; // 'tien_phong', 'phat', 'dich_vu'

    @Column(name = "thang_nam", length = 7)
    private String thangNam; // Format: YYYY-MM

    @Column(name = "so_tien", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal soTien;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "han_thanh_toan")
    private LocalDate hanThanhToan;

    @Column(name = "trang_thai", length = 20)
    private String trangThai; // 'chua_thanh_toan', 'da_thanh_toan', 'qua_han'

    @Column(name = "ma_nv_tao")
    private Integer maNVTao;

    // --- QUAN HỆ ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sv", nullable = false) // Để JPA quản lý hoàn toàn
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hop_dong", nullable = false) // Để JPA quản lý hoàn toàn
    private HopDong hopDong;
}
