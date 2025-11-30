package com.dactaphanmem.service;

import com.dactaphanmem.dto.request.DonDangKyForm;
import com.dactaphanmem.model.*;
import com.dactaphanmem.repository.DonDangKyRepository;
import com.dactaphanmem.repository.SinhVienRepository;
import com.dactaphanmem.repository.ThoiGianDangKyRepository;
import com.dactaphanmem.repository.YeuCauChonPhongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonDangKyService {

    private final DonDangKyRepository donDangKyRepository;
    private final SinhVienRepository sinhVienRepository;
    private final ThoiGianDangKyRepository thoiGianDangKyRepository;
    private final YeuCauChonPhongRepository yeuCauChonPhongRepository;
    private final ThongBaoService thongBaoService;

    @Transactional
    public DonDangKy taoDonDangKy(DonDangKyForm form) {

        // Corrected: Use findByMaSV instead of findById as maSV is no longer the
        // primary key
        SinhVien sinhVien = sinhVienRepository.findByMaSV(form.getMaSV())
                .orElseThrow(() -> new IllegalArgumentException("Mã sinh viên không hợp lệ hoặc không tồn tại."));

        ThoiGianDangKy thoiGianDangKy = thoiGianDangKyRepository.findById(form.getMaDot())
                .orElseThrow(() -> new IllegalArgumentException("Mã đợt đăng ký không hợp lệ."));

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(thoiGianDangKy.getNgayMo()) || now.isAfter(thoiGianDangKy.getNgayDong())) {
            throw new IllegalStateException("Đợt đăng ký hiện không mở.");
        }

        if (donDangKyRepository.findBySinhVienAndThoiGianDangKy(sinhVien, thoiGianDangKy).isPresent()) {
            throw new IllegalStateException("Bạn đã nộp đơn cho đợt đăng ký này rồi.");
        }

        DonDangKy don = new DonDangKy();
        don.setSinhVien(sinhVien);
        don.setThoiGianDangKy(thoiGianDangKy);
        don.setDoiTuongUuTien(form.getDoiTuongUuTien());
        don.setNgayGui(now);
        don.setTrangThai("cho_duyet");
        don.setGhiChu(form.getGhiChu());

        DonDangKy savedDon = donDangKyRepository.save(don);

        if (form.getMaLoaiPhong() != null || form.getMaPhongYeuCau() != null) {
            YeuCauChonPhong yeuCau = new YeuCauChonPhong();
            yeuCau.setDonDangKy(savedDon);
            yeuCau.setMaLoaiPhong(form.getMaLoaiPhong());
            yeuCau.setMaPhong(form.getMaPhongYeuCau());
            yeuCauChonPhongRepository.save(yeuCau);
        }

        // Create notification for admin
        String tieuDe = "Đơn đăng ký mới từ " + sinhVien.getHoTen();
        String noiDung = "Sinh viên " + sinhVien.getHoTen() + " (" + sinhVien.getMaSV()
                + ") vừa nộp đơn đăng ký ký túc xá.";
        thongBaoService.createNotification(tieuDe, noiDung, "DON_DANG_KY", savedDon.getMaDon());

        return savedDon;
    }

    /**
     * Lấy danh sách đơn đăng ký của một sinh viên
     */
    public List<DonDangKy> getDonDangKyBySinhVien(SinhVien sinhVien) {
        return donDangKyRepository.findBySinhVien(sinhVien);
    }

    /**
     * Lấy danh sách đợt đăng ký đang mở
     */
    public List<ThoiGianDangKy> getOpenThoiGianDangKy() {
        LocalDateTime now = LocalDateTime.now();
        return thoiGianDangKyRepository.findByNgayMoBeforeAndNgayDongAfter(now, now);
    }
}
