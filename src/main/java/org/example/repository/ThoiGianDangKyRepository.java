package org.example.repository;

import org.example.model.ThoiGianDangKy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ThoiGianDangKyRepository extends JpaRepository<ThoiGianDangKy, Integer> {
    // Integer là kiểu dữ liệu của MaDot (Primary Key)
    
    // Tìm các đợt đăng ký đang mở (ngày hiện tại nằm giữa ngayMo và ngayDong)
    List<ThoiGianDangKy> findByNgayMoBeforeAndNgayDongAfter(LocalDateTime now1, LocalDateTime now2);
}