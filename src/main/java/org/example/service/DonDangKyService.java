package org.example.service;

import org.example.dto.DonDangKyForm;
import org.example.model.*;
import org.example.repository.DonDangKyRepository;
import org.example.repository.SinhVienRepository;
import org.example.repository.ThoiGianDangKyRepository;
import org.example.repository.YeuCauChonPhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonDangKyService {

    private final DonDangKyRepository donDangKyRepository;
    private final SinhVienRepository sinhVienRepository;
    private final ThoiGianDangKyRepository thoiGianDangKyRepository;
    private final YeuCauChonPhongRepository yeuCauChonPhongRepository;
    private final ThongBaoService thongBaoService;

    public DonDangKyService(DonDangKyRepository donDangKyRepository,
            SinhVienRepository sinhVienRepository,
            ThoiGianDangKyRepository thoiGianDangKyRepository,
            YeuCauChonPhongRepository yeuCauChonPhongRepository,
            ThongBaoService thongBaoService) {
        this.donDangKyRepository = donDangKyRepository;
        this.sinhVienRepository = sinhVienRepository;
        this.thoiGianDangKyRepository = thoiGianDangKyRepository;
        this.yeuCauChonPhongRepository = yeuCauChonPhongRepository;
        this.thongBaoService = thongBaoService;
    }

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
