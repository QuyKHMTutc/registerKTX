package org.example.service;

import org.example.model.ThoiGianDangKy;
import org.example.repository.ThoiGianDangKyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThoiGianDangKyService {

    private final ThoiGianDangKyRepository thoiGianDangKyRepository;

    public ThoiGianDangKyService(ThoiGianDangKyRepository thoiGianDangKyRepository) {
        this.thoiGianDangKyRepository = thoiGianDangKyRepository;
    }

    /**
     * Get all registration periods ordered by start date (newest first)
     */
    public List<ThoiGianDangKy> getAllPeriods() {
        return thoiGianDangKyRepository.findAll();
    }

    /**
     * Get currently open registration periods
     */
    public List<ThoiGianDangKy> getOpenThoiGianDangKy() {
        LocalDateTime now = LocalDateTime.now();
        return thoiGianDangKyRepository.findByNgayMoBeforeAndNgayDongAfter(now, now);
    }

    /**
     * Get a registration period by ID
     */
    public ThoiGianDangKy getPeriodById(Integer maDot) {
        return thoiGianDangKyRepository.findById(maDot)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt đăng ký với mã: " + maDot));
    }

    /**
     * Create a new registration period
     */
    @Transactional
    public ThoiGianDangKy createPeriod(LocalDateTime ngayMo, LocalDateTime ngayDong, String moTa) {
        // Validation
        if (ngayMo == null || ngayDong == null) {
            throw new IllegalArgumentException("Ngày mở và ngày đóng không được để trống");
        }
        if (ngayMo.isAfter(ngayDong)) {
            throw new IllegalArgumentException("Ngày mở phải trước ngày đóng");
        }

        ThoiGianDangKy period = new ThoiGianDangKy();
        period.setNgayMo(ngayMo);
        period.setNgayDong(ngayDong);
        period.setMoTa(moTa);

        return thoiGianDangKyRepository.save(period);
    }

    /**
     * Update an existing registration period
     */
    @Transactional
    public ThoiGianDangKy updatePeriod(Integer maDot, LocalDateTime ngayMo, LocalDateTime ngayDong, String moTa) {
        ThoiGianDangKy period = getPeriodById(maDot);

        // Validation
        if (ngayMo == null || ngayDong == null) {
            throw new IllegalArgumentException("Ngày mở và ngày đóng không được để trống");
        }
        if (ngayMo.isAfter(ngayDong)) {
            throw new IllegalArgumentException("Ngày mở phải trước ngày đóng");
        }

        period.setNgayMo(ngayMo);
        period.setNgayDong(ngayDong);
        period.setMoTa(moTa);

        return thoiGianDangKyRepository.save(period);
    }

    /**
     * Delete a registration period
     */
    @Transactional
    public void deletePeriod(Integer maDot) {
        ThoiGianDangKy period = getPeriodById(maDot);
        thoiGianDangKyRepository.delete(period);
    }

    /**
     * Check if a period is currently open
     */
    public boolean isCurrentlyOpen(ThoiGianDangKy period) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(period.getNgayMo()) && now.isBefore(period.getNgayDong());
    }
}
