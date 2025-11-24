package com.dactaphanmem.repository;

import com.dactaphanmem.model.NhanVien;
import com.dactaphanmem.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    
    /**
     * Tìm nhân viên bằng đối tượng Tài Khoản liên kết.
     */
    Optional<NhanVien> findByTaiKhoan(TaiKhoan taiKhoan);
}
