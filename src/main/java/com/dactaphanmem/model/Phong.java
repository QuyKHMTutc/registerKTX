package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "phong", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "so_phong", "ma_toa" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Phong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "ma_phong")
    Integer maPhong;

    @Column(name = "so_phong")
    String soPhong;

    @Column(name = "tang")
    Integer tang;

    @Column(name = "gioi_tinh")
    String gioiTinh;

    @Column(name = "trang_thai")
    String trangThai;

    // --- QUAN HỆ ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai", nullable = false) // Để JPA quản lý hoàn toàn
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    LoaiPhong loaiPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_toa", nullable = false) // Để JPA quản lý hoàn toàn
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    ToaNha toaNha;
}
