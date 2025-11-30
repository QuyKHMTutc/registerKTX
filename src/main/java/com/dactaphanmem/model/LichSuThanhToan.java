package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_thanh_toan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "hoaDon", "nhanVien" })
@ToString(exclude = { "hoaDon", "nhanVien" })
@EqualsAndHashCode(exclude = { "hoaDon", "nhanVien" })
public class LichSuThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_giao_dich")
    Integer maGiaoDich;

    @Column(name = "so_tien_thanh_toan", nullable = false, columnDefinition = "DECIMAL(10,2)")
    BigDecimal soTienThanhToan;

    @Column(name = "ngay_thanh_toan")
    LocalDateTime ngayThanhToan;

    @Column(name = "phuong_thuc", length = 20)
    String phuongThuc; // 'tien_mat', 'chuyen_khoan', 'online'

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    String ghiChu;

    // --- QUAN HỆ ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hoa_don", nullable = false) // Để JPA quản lý hoàn toàn
    HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nv_xac_nhan") // Có thể null nếu sinh viên tự thanh toán
    NhanVien nhanVien;
}
