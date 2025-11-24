package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal; // Quan trọng cho tiền tệ

@Entity
@Table(name = "loai_phong")
@Data
public class LoaiPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loai")
    private Integer maLoai;

    @Column(name = "ten_loai")
    private String tenLoai;

    @Column(name = "so_nguoi_toi_da")
    private Integer soNguoiToiDa;

    @Column(name = "gia_phong", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal giaPhong;
}