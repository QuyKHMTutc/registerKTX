package org.example.repository;

import org.example.model.SinhVien;
import org.example.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SinhVienRepository extends JpaRepository<SinhVien, String> { // Changed Integer to String
    
    /**
     * Tìm sinh viên bằng đối tượng Tài Khoản liên kết.
     */
    Optional<SinhVien> findByTaiKhoan(TaiKhoan taiKhoan);

    /**
     * Kiểm tra xem mã sinh viên đã tồn tại chưa.
     */
    boolean existsByMaSV(String maSV);

    /**
     * Tìm sinh viên bằng mã sinh viên.
     */
    Optional<SinhVien> findByMaSV(String maSV);
}
