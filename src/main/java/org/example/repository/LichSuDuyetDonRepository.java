package org.example.repository;

import org.example.model.DonDangKy;
import org.example.model.LichSuDuyetDon;
import org.example.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LichSuDuyetDonRepository extends JpaRepository<LichSuDuyetDon, Integer> {
    
    List<LichSuDuyetDon> findByDonDangKy(DonDangKy donDangKy);

    List<LichSuDuyetDon> findByNhanVien(NhanVien nhanVien);
}
