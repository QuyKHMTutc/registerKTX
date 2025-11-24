package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "phong", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "so_phong", "ma_toa" })
})
@Data
public class Phong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phong")
    private Integer maPhong;

    @Column(name = "so_phong")
    private String soPhong;

    @Column(name = "tang")
    private Integer tang;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Column(name = "trang_thai")
    private String trangThai;

    // --- QUAN HỆ ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai", nullable = false) // Để JPA quản lý hoàn toàn
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private LoaiPhong loaiPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_toa", nullable = false) // Để JPA quản lý hoàn toàn
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private ToaNha toaNha;
}
