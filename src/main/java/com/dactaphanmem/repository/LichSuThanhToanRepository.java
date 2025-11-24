package com.dactaphanmem.repository;

import com.dactaphanmem.model.HoaDon;
import com.dactaphanmem.model.LichSuThanhToan;
import com.dactaphanmem.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LichSuThanhToanRepository extends JpaRepository<LichSuThanhToan, Integer> {
    
    List<LichSuThanhToan> findByHoaDon(HoaDon hoaDon);
    
    List<LichSuThanhToan> findByPhuongThuc(String phuongThuc);
    
    List<LichSuThanhToan> findByNhanVien(NhanVien nhanVien);

    List<LichSuThanhToan> findByHoaDonIn(List<HoaDon> hoaDonList);
}
