package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "thoi_gian_dang_ky")
@Data
public class ThoiGianDangKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dot")
    private Integer maDot;

    @Column(name = "ngay_mo")
    private LocalDateTime ngayMo;

    @Column(name = "ngay_dong")
    private LocalDateTime ngayDong;

    @Column(name = "mo_ta")
    private String moTa;

    // Lưu ý: Nếu có các mối quan hệ One-to-Many với DonDangKy,
    // bạn cần thêm @JsonIgnore vào List<DonDangKy> tại đây.
}