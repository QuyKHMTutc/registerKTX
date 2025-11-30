package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal; // Quan trọng cho tiền tệ

@Entity
@Table(name = "loai_phong")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoaiPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loai")
    Integer maLoai;

    @Column(name = "ten_loai")
    String tenLoai;

    @Column(name = "so_nguoi_toi_da")
    Integer soNguoiToiDa;

    @Column(name = "gia_phong", columnDefinition = "DECIMAL(10,2)")
    BigDecimal giaPhong;
}