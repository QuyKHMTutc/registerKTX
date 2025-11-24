package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "nhan_vien")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "taiKhoan"})
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nv")
    private Integer maNV;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    // Mỗi nhân viên được liên kết với một tài khoản duy nhất
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tk", unique = true)
    private TaiKhoan taiKhoan;
}
