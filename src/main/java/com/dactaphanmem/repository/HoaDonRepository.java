package com.dactaphanmem.repository;

import com.dactaphanmem.model.HoaDon;
import com.dactaphanmem.model.HopDong;
import com.dactaphanmem.model.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    
    List<HoaDon> findBySinhVien(SinhVien sinhVien);

    List<HoaDon> findByHopDong(HopDong hopDong);
}
