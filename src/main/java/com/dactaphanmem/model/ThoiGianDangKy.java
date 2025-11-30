package com.dactaphanmem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "thoi_gian_dang_ky")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThoiGianDangKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "ma_dot")
    Integer maDot;

    @Column(name = "ngay_mo")
    LocalDateTime ngayMo;

    @Column(name = "ngay_dong")
    LocalDateTime ngayDong;

    @Column(name = "mo_ta")
    String moTa;

    // Lưu ý: Nếu có các mối quan hệ One-to-Many với DonDangKy,
    // bạn cần thêm @JsonIgnore vào List<DonDangKy> tại đây.
}