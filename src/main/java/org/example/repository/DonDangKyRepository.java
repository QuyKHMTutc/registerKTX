package org.example.repository;

import org.example.model.DonDangKy;
import org.example.model.SinhVien;
import org.example.model.ThoiGianDangKy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DonDangKyRepository extends JpaRepository<DonDangKy, Integer> {
    
    List<DonDangKy> findByTrangThai(String trangThai);

    Optional<DonDangKy> findBySinhVienAndThoiGianDangKy(SinhVien sinhVien, ThoiGianDangKy thoiGianDangKy);

    List<DonDangKy> findBySinhVien(SinhVien sinhVien);
}
