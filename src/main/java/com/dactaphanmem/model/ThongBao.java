package com.dactaphanmem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thong_bao")
    Integer maThongBao;

    @Column(name = "tieu_de", nullable = false)
    String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    String noiDung;

    @Column(name = "loai", length = 50)
    String loai; // "DON_DANG_KY", "THANH_TOAN", "HOP_DONG", etc.

    @Column(name = "da_doc", nullable = false)
    @Builder.Default
    Boolean daDoc = false;

    @Column(name = "ngay_tao", nullable = false)
    LocalDateTime ngayTao;

    @Column(name = "ma_don")
    Integer maDon; // Reference to DonDangKy ID

    @Column(name = "ma_nguoi_nhan")
    Integer maNguoiNhan; // Admin ID (nullable for broadcast to all admins)

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
