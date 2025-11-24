package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "yeu_cau_chon_phong")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "donDangKy", "loaiPhong", "phong"})
@ToString(exclude = {"donDangKy", "loaiPhong", "phong"})
@EqualsAndHashCode(exclude = {"donDangKy", "loaiPhong", "phong"})
public class YeuCauChonPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_yc")
    private Integer maYC;

    @Column(name = "ma_loai_phong")
    private Integer maLoaiPhong;

    @Column(name = "ma_phong")
    private Integer maPhong; // NULL nếu chỉ yêu cầu loại phòng, không yêu cầu phòng cụ thể

    // --- QUAN HỆ ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don", nullable = false, unique = true) // Để JPA quản lý hoàn toàn
    private DonDangKy donDangKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai_phong", insertable = false, updatable = false) // Vẫn giữ insertable/updatable false vì ma_loai_phong là cột riêng
    private LoaiPhong loaiPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", insertable = false, updatable = false) // Vẫn giữ insertable/updatable false vì ma_phong là cột riêng
    private Phong phong;
}
