package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thong_bao")
    private Integer maThongBao;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "loai", length = 50)
    private String loai; // "DON_DANG_KY", "THANH_TOAN", "HOP_DONG", etc.

    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc = false;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ma_don")
    private Integer maDon; // Reference to DonDangKy ID

    @Column(name = "ma_nguoi_nhan")
    private Integer maNguoiNhan; // Admin ID (nullable for broadcast to all admins)

    @PrePersist
    protected void onCreate() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (daDoc == null) {
            daDoc = false;
        }
    }
}
