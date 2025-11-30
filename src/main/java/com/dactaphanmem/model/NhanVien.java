package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "nhan_vien")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "taiKhoan" })
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nv")
    Integer maNV;

    @Column(name = "ho_ten", nullable = false)
    String hoTen;

    // Mỗi nhân viên được liên kết với một tài khoản duy nhất
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tk", unique = true)
    TaiKhoan taiKhoan;
}
