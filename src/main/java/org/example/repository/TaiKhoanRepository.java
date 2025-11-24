package org.example.repository;

import org.example.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    // Tìm tài khoản theo tên đăng nhập
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    
    // Kiểm tra tên đăng nhập đã tồn tại chưa
    boolean existsByTenDangNhap(String tenDangNhap);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Tìm tài khoản theo email
    Optional<TaiKhoan> findByEmail(String email);
    
    // Tìm tất cả tài khoản theo loại
    java.util.List<TaiKhoan> findByLoaiTK(String loaiTK);
}
