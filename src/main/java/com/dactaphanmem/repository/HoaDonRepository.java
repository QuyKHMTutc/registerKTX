package com.dactaphanmem.repository;

import com.dactaphanmem.model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate; // Changed back to LocalDate

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findBySinhVien_MaSV(String maSV);

    /**
     * Finds all unpaid invoices where the due date is before the current time.
     * @param currentTime The current time to check against.
     * @param trangThai The status of the invoices to look for (e.g., "chua_thanh_toan").
     * @return A list of overdue invoices.
     */
    // Removed: List<HoaDon> findByTrangThaiAndHanThanhToanBefore(String trangThai, LocalDateTime currentTime);
    List<HoaDon> findByTrangThaiAndHanThanhToanBefore(String trangThai, LocalDate currentTime);
}
