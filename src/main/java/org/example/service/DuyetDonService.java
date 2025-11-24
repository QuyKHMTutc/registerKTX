// java
package org.example.service;

import org.example.model.*;
import org.example.repository.*;
import org.example.constant.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DuyetDonService {

    private final DonDangKyRepository donDangKyRepository;
    private final PhongRepository phongRepository;
    private final HopDongRepository hopDongRepository;
    private final LichSuDuyetDonRepository lichSuDuyetDonRepository;
    private final HoaDonRepository hoaDonRepository;
    private final YeuCauChonPhongRepository yeuCauChonPhongRepository;
    private final SinhVienRepository sinhVienRepository;
    private final NhanVienRepository nhanVienRepository;

    public DuyetDonService(DonDangKyRepository donDangKyRepository,
                           PhongRepository phongRepository,
                           HopDongRepository hopDongRepository,
                           LichSuDuyetDonRepository lichSuDuyDonRepository,
                           HoaDonRepository hoaDonRepository,
                           YeuCauChonPhongRepository yeuCauChonPhongRepository,
                           SinhVienRepository sinhVienRepository,
                           NhanVienRepository nhanVienRepository) {
        this.donDangKyRepository = donDangKyRepository;
        this.phongRepository = phongRepository;
        this.hopDongRepository = hopDongRepository;
        this.lichSuDuyetDonRepository = lichSuDuyDonRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.yeuCauChonPhongRepository = yeuCauChonPhongRepository;
        this.sinhVienRepository = sinhVienRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    public List<DonDangKy> getPendingApplications() {
        return donDangKyRepository.findByTrangThai(AppConstants.DON_CHO_DUYET);
    }

    public List<Phong> getAvailableRooms(String maSV) {
        // Sửa lỗi: Tìm sinh viên trực tiếp bằng maSV (String)
        SinhVien sv = sinhVienRepository.findById(maSV)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với mã: " + maSV));
        return phongRepository.findByTrangThaiAndGioiTinh(AppConstants.PHONG_SAN_SANG, sv.getGioiTinh());
    }

    private void normalizeDoiTuongUuTien(DonDangKy don) {
        String currentDoiTuong = don.getDoiTuongUuTien();
        if (currentDoiTuong == null) {
            don.setDoiTuongUuTien("binh_thuong");
            return;
        }
        switch (currentDoiTuong) {
            case "1": case "hoan_canh_kho_khan": don.setDoiTuongUuTien("ho_ngheo"); break;
            case "2": case "con_thuong_binh": don.setDoiTuongUuTien("con_thuong_binh"); break;
            case "3": case "nguoi_khuyet_tat": don.setDoiTuongUuTien("khuyet_tat"); break;
            case "0": case "4": case "sinh_vien_xa_nha": case "khong_uu_tien": don.setDoiTuongUuTien("binh_thuong"); break;
            case "ho_ngheo": case "khuyet_tat": case "binh_thuong": break;
            default: don.setDoiTuongUuTien("binh_thuong"); break;
        }
    }

    @Transactional
    public HopDong approveApplicationAutomatically(DonDangKy don, Integer maNV) { // Sửa tham số
        SinhVien sv = don.getSinhVien();

        Optional<YeuCauChonPhong> yeuCauOpt = yeuCauChonPhongRepository.findByDonDangKy(don);
        if (yeuCauOpt.isPresent() && yeuCauOpt.get().getMaPhong() != null) {
            Integer maPhongYeuCau = yeuCauOpt.get().getMaPhong();
            Optional<Phong> phongYeuCauOpt = phongRepository.findById(maPhongYeuCau);
            if (phongYeuCauOpt.isPresent() && AppConstants.PHONG_SAN_SANG.equals(phongYeuCauOpt.get().getTrangThai())) {
                return approveApplicationWithManualRoom(don, maNV, maPhongYeuCau);
            }
        }

        List<Phong> availableRooms = getAvailableRooms(sv.getMaSV());
        if (availableRooms.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy phòng nào phù hợp cho sinh viên này.");
        }

        Phong phongDeXuat = availableRooms.get(0);
        return approveApplicationWithManualRoom(don, maNV, phongDeXuat.getMaPhong());
    }

    @Transactional
    public HopDong approveApplicationWithManualRoom(DonDangKy don, Integer maNV, Integer maPhongChiDinh) {
        if (!AppConstants.DON_CHO_DUYET.equals(don.getTrangThai())) {
            throw new IllegalStateException("Đơn đã được xử lý.");
        }

        normalizeDoiTuongUuTien(don);

        SinhVien sv = don.getSinhVien();
        // SỬ DỤNG LOCK Ở ĐÂY ĐỂ TRÁNH RACE CONDITION
        Phong phong = phongRepository.findByIdWithLock(maPhongChiDinh)
                .orElseThrow(() -> new IllegalArgumentException("Mã Phòng chỉ định không tồn tại."));

        NhanVien nhanVien = nhanVienRepository.findById(maNV)
                .orElseThrow(() -> new IllegalArgumentException("Mã Nhân viên không hợp lệ."));

        if (!AppConstants.PHONG_SAN_SANG.equals(phong.getTrangThai())) {
            throw new IllegalStateException("Phòng " + phong.getSoPhong() + " đã đầy hoặc đang bảo trì.");
        }

        // Kiểm tra null để tránh NullPointerException
        String gioiTinhSV = sv.getGioiTinh();
        String gioiTinhPhong = phong.getGioiTinh();
        
        if (gioiTinhSV == null || gioiTinhSV.isBlank()) {
            throw new IllegalStateException("Sinh viên chưa có thông tin giới tính.");
        }
        
        if (gioiTinhPhong == null || gioiTinhPhong.isBlank()) {
            throw new IllegalStateException("Phòng " + phong.getSoPhong() + " chưa có thông tin giới tính.");
        }

        if (!gioiTinhSV.equalsIgnoreCase(gioiTinhPhong) && !AppConstants.GIOI_TINH_KHONG_YEU_CAU.equalsIgnoreCase(gioiTinhPhong)) {
            throw new IllegalStateException("Giới tính của sinh viên (" + gioiTinhSV + ") không phù hợp với phòng (" + gioiTinhPhong + ").");
        }

        if (phong.getLoaiPhong() == null) {
            throw new IllegalStateException("Lỗi dữ liệu: Phòng " + phong.getSoPhong() + " không có thông tin về loại phòng.");
        }

        don.setTrangThai(AppConstants.DON_DA_DUYET);
        don.setGhiChu("Đơn được duyệt. Gán Phòng: " + phong.getSoPhong());
        donDangKyRepository.save(don);

        LichSuDuyetDon ls = new LichSuDuyetDon();
        ls.setDonDangKy(don);
        ls.setNhanVien(nhanVien);
        ls.setTrangThaiMoi(AppConstants.DON_DA_DUYET);
        ls.setGhiChu("Đơn được duyệt và tạo hợp đồng.");
        ls.setThoiGian(LocalDateTime.now());
        lichSuDuyetDonRepository.save(ls);

        HopDong hd = new HopDong();
        hd.setDonDangKy(don);
        hd.setSinhVien(sv);
        hd.setPhong(phong);
        hd.setNgayBatDau(LocalDate.now()); // Sửa lỗi cú pháp
        hd.setNgayKetThuc(LocalDate.now().plusMonths(6));
        BigDecimal giaPhong = phong.getLoaiPhong().getGiaPhong();
        hd.setGiaThucTe(giaPhong);
        hd.setTrangThai(AppConstants.HOP_DONG_CHO_THANH_TOAN);
        HopDong savedHopDong = hopDongRepository.save(hd);

        updatePhongStatus(phong);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setHopDong(savedHopDong);
        hoaDon.setSinhVien(sv);
        hoaDon.setTieuDe("Tiền phòng tháng " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        hoaDon.setLoaiHoaDon("tien_phong");
        hoaDon.setThangNam(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        hoaDon.setSoTien(giaPhong);
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setHanThanhToan(LocalDate.now().plusDays(7));
        hoaDon.setTrangThai(AppConstants.HOA_DON_CHUA_THANH_TOAN);
        hoaDon.setMaNVTao(maNV);
        hoaDonRepository.save(hoaDon);

        return savedHopDong;
    }

    @Transactional
    public DonDangKy rejectApplication(DonDangKy don, Integer maNV, String lyDoTuChoi) { // Sửa tham số
        if (!AppConstants.DON_CHO_DUYET.equals(don.getTrangThai())) {
            throw new IllegalStateException("Đơn đã được xử lý.");
        }

        normalizeDoiTuongUuTien(don);

        NhanVien nhanVien = nhanVienRepository.findById(maNV)
                .orElseThrow(() -> new IllegalArgumentException("Mã Nhân viên không hợp lệ."));

        don.setTrangThai(AppConstants.DON_TU_CHOI);
        don.setGhiChu(lyDoTuChoi);
        donDangKyRepository.save(don);

        LichSuDuyetDon ls = new LichSuDuyetDon();
        ls.setDonDangKy(don);
        ls.setNhanVien(nhanVien);
        ls.setTrangThaiMoi(AppConstants.DON_TU_CHOI);
        ls.setGhiChu(lyDoTuChoi);
        ls.setThoiGian(LocalDateTime.now());
        lichSuDuyetDonRepository.save(ls);

        return don;
    }

    private void updatePhongStatus(Phong phong) {
        if (phong == null || phong.getLoaiPhong() == null) {
            return;
        }
        List<String> activeStatus = Arrays.asList(AppConstants.HOP_DONG_CHO_THANH_TOAN, AppConstants.HOP_DONG_HIEU_LUC);
        long soNguoiHienTai = hopDongRepository.countByPhongAndTrangThaiIn(phong, activeStatus);

        if (soNguoiHienTai >= phong.getLoaiPhong().getSoNguoiToiDa()) {
            phong.setTrangThai(AppConstants.PHONG_DAY_CHO);
        } else {
            phong.setTrangThai(AppConstants.PHONG_SAN_SANG);
        }
        phongRepository.save(phong);
    }

    /**
     * Lấy đơn đăng ký theo mã đơn
     */
    public DonDangKy getDonDangKyById(Integer maDon) {
        return donDangKyRepository.findById(maDon)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đăng ký với mã: " + maDon));
    }
}
