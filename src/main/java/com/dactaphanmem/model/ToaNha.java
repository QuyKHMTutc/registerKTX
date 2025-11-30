package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "toa_nha")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToaNha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "ma_toa")
    Integer maToa;

    @Column(name = "ten_toa")
    String tenToa;

    @Column(name = "so_tang")
    Integer soTang;

    // loai_toa: ENUM('Nam', 'Nu')
    @Column(name = "loai_toa", nullable = false, length = 3)
    String loaiToa; // "Nam" hoặc "Nu"

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    String moTa;

    public enum LoaiToa {
        Nam, Nu
    }
}