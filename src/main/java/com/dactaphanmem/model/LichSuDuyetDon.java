package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_duyet_don")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "nhanVien", "donDangKy" })
@ToString(exclude = { "nhanVien", "donDangKy" })
@EqualsAndHashCode(exclude = { "nhanVien", "donDangKy" })
public class LichSuDuyetDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ls")
    Integer maLS;

    @Column(name = "trang_thai_moi")
    String trangThaiMoi;

    @Column(name = "ghi_chu")
    String ghiChu;

    @Column(name = "thoi_gian")
    LocalDateTime thoiGian;

    // --- QUAN HỆ ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don", nullable = false) // Để JPA quản lý hoàn toàn
    DonDangKy donDangKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nv", nullable = false) // Để JPA quản lý hoàn toàn
    NhanVien nhanVien;
}
