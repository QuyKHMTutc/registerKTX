package org.example.service;

import org.example.model.HoaDon;
import org.example.model.HopDong;
import org.example.model.LichSuThanhToan;
import org.example.model.NhanVien;
import org.example.constant.AppConstants;
import org.example.repository.HoaDonRepository;
import org.example.repository.HopDongRepository;
import org.example.repository.LichSuThanhToanRepository;
import org.example.repository.NhanVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ThanhToanService {

    private final HoaDonRepository hoaDonRepository;
    private final HopDongRepository hopDongRepository;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final NhanVienRepository nhanVienRepository;

    public ThanhToanService(HoaDonRepository hoaDonRepository,
                           HopDongRepository hopDongRepository,
                           LichSuThanhToanRepository lichSuThanhToanRepository,
                           NhanVienRepository nhanVienRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.hopDongRepository = hopDongRepository;
        this.lichSuThanhToanRepository = lichSuThanhToanRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @Transactional
    public LichSuThanhToan thanhToanHoaDon(Integer maHoaDon, String phuongThucThanhToan, Integer maNVXacNhan) {
        // 1. Tìm hóa đơn
        HoaDon hoaDon = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy hóa đơn với mã: " + maHoaDon));

        if (AppConstants.HOA_DON_DA_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new IllegalStateException("Hóa đơn này đã được thanh toán trước đó.");
        }

        // 2. Tìm hợp đồng liên quan
        HopDong hopDong = hoaDon.getHopDong(); // Lấy trực tiếp từ đối tượng HoaDon
        if (hopDong == null) {
            throw new IllegalStateException("Không tìm thấy hợp đồng liên quan đến hóa đơn.");
        }

        // 3. Cập nhật trạng thái hóa đơn
        hoaDon.setTrangThai(AppConstants.HOA_DON_DA_THANH_TOAN);
        hoaDonRepository.save(hoaDon);

        // 4. Cập nhật trạng thái hợp đồng
        if (AppConstants.HOP_DONG_CHO_THANH_TOAN.equals(hopDong.getTrangThai())) {
            hopDong.setTrangThai(AppConstants.HOP_DONG_HIEU_LUC);
            hopDongRepository.save(hopDong);
        }

        // 5. Tạo lịch sử thanh toán
        LichSuThanhToan lichSu = new LichSuThanhToan();
        lichSu.setHoaDon(hoaDon); // Gán đối tượng
        lichSu.setSoTienThanhToan(hoaDon.getSoTien());
        lichSu.setNgayThanhToan(LocalDateTime.now());
        lichSu.setPhuongThuc(phuongThucThanhToan);
        
        if (maNVXacNhan != null) {
            NhanVien nhanVien = nhanVienRepository.findById(maNVXacNhan)
                    .orElseThrow(() -> new IllegalArgumentException("Mã Nhân viên xác nhận không hợp lệ."));
            lichSu.setNhanVien(nhanVien); // Gán đối tượng
        }
        
        lichSu.setGhiChu("Thanh toán cho hóa đơn #" + maHoaDon);

        return lichSuThanhToanRepository.save(lichSu);
    }
}
