package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "sinh_vien")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "taiKhoan"})
@ToString(exclude = {"taiKhoan"})
@EqualsAndHashCode(exclude = {"taiKhoan"})
public class SinhVien {

    @Id
    @Column(name = "ma_sv", unique = true, nullable = false)
    private String maSV;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "lop")
    private String lop;

    @Column(name = "khoa")
    private String khoa;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "dia_chi")
    private String diaChi;

    // --- QUAN HỆ ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tk", unique = true, nullable = false) // Link to TaiKhoan
    private TaiKhoan taiKhoan;
}
