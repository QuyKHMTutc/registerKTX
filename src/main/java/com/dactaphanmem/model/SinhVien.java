package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "sinh_vien")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "taiKhoan" })
@ToString(exclude = { "taiKhoan" })
@EqualsAndHashCode(exclude = { "taiKhoan" })
public class SinhVien {

    @Id
    @Column(name = "ma_sv", unique = true, nullable = false)
    String maSV;

    @Column(name = "ho_ten")
    String hoTen;

    @Column(name = "lop")
    String lop;

    @Column(name = "khoa")
    String khoa;

    @Column(name = "sdt")
    String sdt;

    @Column(name = "gioi_tinh")
    String gioiTinh;

    @Column(name = "ngay_sinh")
    LocalDate ngaySinh;

    @Column(name = "dia_chi")
    String diaChi;

    // --- QUAN HỆ ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tk", unique = true, nullable = false) // Link to TaiKhoan
    TaiKhoan taiKhoan;
}
