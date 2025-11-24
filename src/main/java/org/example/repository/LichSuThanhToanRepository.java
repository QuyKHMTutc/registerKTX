package org.example.repository;

import org.example.model.HoaDon;
import org.example.model.LichSuThanhToan;
import org.example.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LichSuThanhToanRepository extends JpaRepository<LichSuThanhToan, Integer> {
    
    List<LichSuThanhToan> findByHoaDon(HoaDon hoaDon);
    
    List<LichSuThanhToan> findByPhuongThuc(String phuongThuc);
    
    List<LichSuThanhToan> findByNhanVien(NhanVien nhanVien);

    List<LichSuThanhToan> findByHoaDonIn(List<HoaDon> hoaDonList);
}
