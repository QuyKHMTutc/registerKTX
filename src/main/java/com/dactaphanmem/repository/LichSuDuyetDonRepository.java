package com.dactaphanmem.repository;

import com.dactaphanmem.model.DonDangKy;
import com.dactaphanmem.model.LichSuDuyetDon;
import com.dactaphanmem.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LichSuDuyetDonRepository extends JpaRepository<LichSuDuyetDon, Integer> {
    
    List<LichSuDuyetDon> findByDonDangKy(DonDangKy donDangKy);

    List<LichSuDuyetDon> findByNhanVien(NhanVien nhanVien);
}
