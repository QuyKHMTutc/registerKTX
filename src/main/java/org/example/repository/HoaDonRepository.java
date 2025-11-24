package org.example.repository;

import org.example.model.HoaDon;
import org.example.model.HopDong;
import org.example.model.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    
    List<HoaDon> findBySinhVien(SinhVien sinhVien);

    List<HoaDon> findByHopDong(HopDong hopDong);
}
