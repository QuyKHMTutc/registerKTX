package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_duyet_don")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "nhanVien", "donDangKy"})
@ToString(exclude = {"nhanVien", "donDangKy"})
@EqualsAndHashCode(exclude = {"nhanVien", "donDangKy"})
public class LichSuDuyetDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ls")
    private Integer maLS;

    @Column(name = "trang_thai_moi")
    private String trangThaiMoi;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    // --- QUAN HỆ ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don", nullable = false) // Để JPA quản lý hoàn toàn
    private DonDangKy donDangKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nv", nullable = false) // Để JPA quản lý hoàn toàn
    private NhanVien nhanVien;
}
