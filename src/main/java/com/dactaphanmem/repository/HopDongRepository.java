package com.dactaphanmem.repository;

import com.dactaphanmem.model.HopDong;
import com.dactaphanmem.model.Phong;
import com.dactaphanmem.model.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HopDongRepository extends JpaRepository<HopDong, Integer> {
    
    /**
     * Tìm tất cả hợp đồng của một sinh viên.
     */
    List<HopDong> findBySinhVien(SinhVien sinhVien);

    /**
     * Tìm tất cả hợp đồng liên quan đến một phòng.
     */
    List<HopDong> findByPhong(Phong phong);

    /**
     * Đếm số hợp đồng đang hoạt động trong một phòng.
     * "Hoạt động" được định nghĩa là có trạng thái nằm trong danh sách được cung cấp.
     */
    long countByPhongAndTrangThaiIn(Phong phong, List<String> trangThai);
}
