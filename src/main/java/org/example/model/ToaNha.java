package org.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "toa_nha")
@Data
public class ToaNha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_toa")
    private Integer maToa;

    @Column(name = "ten_toa")
    private String tenToa;

    @Column(name = "so_tang")
    private Integer soTang;

    // loai_toa: ENUM('Nam', 'Nu')
    @Column(name = "loai_toa", nullable = false, length = 3)
    private String loaiToa; // "Nam" hoặc "Nu"

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    public enum LoaiToa { Nam, Nu }
}