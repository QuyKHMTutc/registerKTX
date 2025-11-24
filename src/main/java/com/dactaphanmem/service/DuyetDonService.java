// java
package com.dactaphanmem.service;

import com.dactaphanmem.model.*;
import com.dactaphanmem.repository.*;
import com.dactaphanmem.constant.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * Lấy danh sách các đơn đăng ký đang chờ duyệt, đã được sắp xếp theo thứ tự ưu tiên.
     * Thứ tự ưu tiên: Khuyết tật > Hộ nghèo > Con thương binh > Bình thường.
     * 
     * @return List<DonDangKy> danh sách đơn chờ duyệt đã sắp xếp.
     */
    public List<DonDangKy> getPendingApplications() {
        List<DonDangKy> pendingApplications = donDangKyRepository.findByTrangThai(AppConstants.DON_CHO_DUYET);

        // Chuẩn hóa đối tượng ưu tiên cho tất cả các đơn trước khi sắp xếp
        pendingApplications.forEach(this::normalizeDoiTuongUuTien);

        // Định nghĩa thứ tự ưu tiên
        Map<String, Integer> priorityOrder = Map.of(
            "khuyet_tat", 1,
            "ho_ngheo", 2,
            "con_thuong_binh", 3,
            "binh_thuong", 4
        );

        // Sắp xếp danh sách
        return pendingApplications.stream()
            .sorted(Comparator.comparing(don -> priorityOrder.getOrDefault(don.getDoiTuongUuTien(), 5)))
            .collect(Collectors.toList());
    }

    /**
     * Tìm các phòng còn trống phù hợp với giới tính của sinh viên.
     * 
     * @param maSV Mã sinh viên cần tìm phòng.
     * @return List<Phong> danh sách phòng khả dụng.
     */
    public List<Phong> getAvailableRooms(String maSV) {
        SinhVien sv = sinhVienRepository.findById(maSV)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với mã: " + maSV));
        return phongRepository.findByTrangThaiAndGioiTinh(AppConstants.PHONG_SAN_SANG, sv.getGioiTinh());
    }

    /**
     * Chuẩn hóa đối tượng ưu tiên về các nhóm chính để xử lý logic.
     * 
     * @param don Đơn đăng ký cần chuẩn hóa.
     */
    private void normalizeDoiTuongUuTien(DonDangKy don) {
        String currentDoiTuong = don.getDoiTuongUuTien();
        if (currentDoiTuong == null) {
            don.setDoiTuongUuTien("binh_thuong");
            return;
        }

        // Sử dụng switch expression (Java 14+) hoặc logic if-else gọn gàng hơn
        switch (currentDoiTuong) {
            case "1":
            case "hoan_canh_kho_khan":
                don.setDoiTuongUuTien("ho_ngheo");
                break;
            case "2":
            case "con_thuong_binh":
                don.setDoiTuongUuTien("con_thuong_binh");
                break;
            case "3":
            case "nguoi_khuyet_tat":
                don.setDoiTuongUuTien("khuyet_tat");
                break;
            case "0":
            case "4":
            case "sinh_vien_xa_nha":
            case "khong_uu_tien":
                don.setDoiTuongUuTien("binh_thuong");
                break;
            case "ho_ngheo":
            case "khuyet_tat":
            case "binh_thuong":
                // Đã chuẩn hóa, không cần thay đổi
                break;
            default:
                don.setDoiTuongUuTien("binh_thuong");
                break;
        }
    }

    /**
     * Duyệt đơn đăng ký tự động (hệ thống tự chọn phòng).
     * 
     * @param don  Đơn đăng ký cần duyệt.
     * @param maNV Mã nhân viên thực hiện duyệt.
     * @return HopDong Hợp đồng mới được tạo.
     */
    @Transactional
    public HopDong approveApplicationAutomatically(DonDangKy don, Integer maNV) {
        SinhVien sv = don.getSinhVien();

        // Kiểm tra xem sinh viên có yêu cầu chọn phòng cụ thể không
        Optional<YeuCauChonPhong> yeuCauOpt = yeuCauChonPhongRepository.findByDonDangKy(don);
        if (yeuCauOpt.isPresent() && yeuCauOpt.get().getMaPhong() != null) {
            Integer maPhongYeuCau = yeuCauOpt.get().getMaPhong();
            Optional<Phong> phongYeuCauOpt = phongRepository.findById(maPhongYeuCau);
            // Nếu phòng yêu cầu còn trống, ưu tiên xếp vào đó
            if (phongYeuCauOpt.isPresent() && AppConstants.PHONG_SAN_SANG.equals(phongYeuCauOpt.get().getTrangThai())) {
                return approveApplicationWithManualRoom(don, maNV, maPhongYeuCau);
            }
        }

        // Nếu không có yêu cầu hoặc phòng yêu cầu không khả dụng, tìm phòng trống bất
        // kỳ
        List<Phong> availableRooms = getAvailableRooms(sv.getMaSV());
        if (availableRooms.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy phòng nào phù hợp cho sinh viên này.");
        }

        Phong phongDeXuat = availableRooms.get(0);
        return approveApplicationWithManualRoom(don, maNV, phongDeXuat.getMaPhong());
    }

    /**
     * Duyệt đơn đăng ký với phòng được chỉ định thủ công.
     * 
     * @param don            Đơn đăng ký.
     * @param maNV           Mã nhân viên duyệt.
     * @param maPhongChiDinh Mã phòng được chọn.
     * @return HopDong Hợp đồng mới.
     */
    @Transactional
    public HopDong approveApplicationWithManualRoom(DonDangKy don, Integer maNV, Integer maPhongChiDinh) {
        if (!AppConstants.DON_CHO_DUYET.equals(don.getTrangThai())) {
            throw new IllegalStateException("Đơn này đã được xử lý trước đó.");
        }

        normalizeDoiTuongUuTien(don);

        SinhVien sv = don.getSinhVien();
        // Sử dụng lock để tránh race condition khi nhiều admin cùng duyệt vào một phòng
        Phong phong = phongRepository.findByIdWithLock(maPhongChiDinh)
                .orElseThrow(() -> new IllegalArgumentException("Mã Phòng chỉ định không tồn tại."));

        NhanVien nhanVien = nhanVienRepository.findById(maNV)
                .orElseThrow(() -> new IllegalArgumentException("Mã Nhân viên không hợp lệ."));

        if (!AppConstants.PHONG_SAN_SANG.equals(phong.getTrangThai())) {
            throw new IllegalStateException("Phòng " + phong.getSoPhong() + " đã đầy hoặc đang bảo trì.");
        }

        validateGenderCompatibility(sv, phong);

        if (phong.getLoaiPhong() == null) {
            throw new IllegalStateException(
                    "Lỗi dữ liệu: Phòng " + phong.getSoPhong() + " không có thông tin về loại phòng.");
        }

        // Cập nhật trạng thái đơn
        don.setTrangThai(AppConstants.DON_DA_DUYET);
        don.setGhiChu("Đơn được duyệt. Gán Phòng: " + phong.getSoPhong());
        donDangKyRepository.save(don);

        // Ghi lịch sử
        createApprovalHistory(don, nhanVien, AppConstants.DON_DA_DUYET, "Đơn được duyệt và tạo hợp đồng.");

        // Tạo hợp đồng
        HopDong hd = createContract(don, sv, phong);
        HopDong savedHopDong = hopDongRepository.save(hd);

        // Cập nhật trạng thái phòng
        updatePhongStatus(phong);

        // Tạo hóa đơn đầu tiên
        createFirstInvoice(savedHopDong, sv, maNV, phong.getLoaiPhong().getGiaPhong());

        return savedHopDong;
    }

    /**
     * Từ chối đơn đăng ký.
     * 
     * @param don        Đơn đăng ký.
     * @param maNV       Mã nhân viên từ chối.
     * @param lyDoTuChoi Lý do từ chối.
     * @return DonDangKy Đơn đã cập nhật.
     */
    @Transactional
    public DonDangKy rejectApplication(DonDangKy don, Integer maNV, String lyDoTuChoi) {
        if (!AppConstants.DON_CHO_DUYET.equals(don.getTrangThai())) {
            throw new IllegalStateException("Đơn này đã được xử lý trước đó.");
        }

        normalizeDoiTuongUuTien(don);

        NhanVien nhanVien = nhanVienRepository.findById(maNV)
                .orElseThrow(() -> new IllegalArgumentException("Mã Nhân viên không hợp lệ."));

        don.setTrangThai(AppConstants.DON_TU_CHOI);
        don.setGhiChu(lyDoTuChoi);
        donDangKyRepository.save(don);

        createApprovalHistory(don, nhanVien, AppConstants.DON_TU_CHOI, lyDoTuChoi);

        return don;
    }

    // --- Helper Methods ---

    private void validateGenderCompatibility(SinhVien sv, Phong phong) {
        String gioiTinhSV = sv.getGioiTinh();
        String gioiTinhPhong = phong.getGioiTinh();

        if (gioiTinhSV == null || gioiTinhSV.isBlank()) {
            throw new IllegalStateException("Sinh viên chưa có thông tin giới tính.");
        }

        if (gioiTinhPhong == null || gioiTinhPhong.isBlank()) {
            throw new IllegalStateException("Phòng " + phong.getSoPhong() + " chưa có thông tin giới tính.");
        }

        if (!gioiTinhSV.equalsIgnoreCase(gioiTinhPhong)
                && !AppConstants.GIOI_TINH_KHONG_YEU_CAU.equalsIgnoreCase(gioiTinhPhong)) {
            throw new IllegalStateException(
                    "Giới tính của sinh viên (" + gioiTinhSV + ") không phù hợp với phòng (" + gioiTinhPhong + ").");
        }
    }

    private void createApprovalHistory(DonDangKy don, NhanVien nv, String trangThai, String ghiChu) {
        LichSuDuyetDon ls = new LichSuDuyetDon();
        ls.setDonDangKy(don);
        ls.setNhanVien(nv);
        ls.setTrangThaiMoi(trangThai);
        ls.setGhiChu(ghiChu);
        ls.setThoiGian(LocalDateTime.now());
        lichSuDuyetDonRepository.save(ls);
    }

    private HopDong createContract(DonDangKy don, SinhVien sv, Phong phong) {
        HopDong hd = new HopDong();
        hd.setDonDangKy(don);
        hd.setSinhVien(sv);
        hd.setPhong(phong);
        hd.setNgayBatDau(LocalDate.now());
        hd.setNgayKetThuc(LocalDate.now().plusMonths(6)); // Mặc định 6 tháng
        hd.setGiaThucTe(phong.getLoaiPhong().getGiaPhong());
        hd.setTrangThai(AppConstants.HOP_DONG_CHO_THANH_TOAN);
        return hd;
    }

    private void createFirstInvoice(HopDong hd, SinhVien sv, Integer maNV, BigDecimal soTien) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setHopDong(hd);
        hoaDon.setSinhVien(sv);
        hoaDon.setTieuDe("Tiền phòng tháng " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        hoaDon.setLoaiHoaDon("tien_phong");
        hoaDon.setThangNam(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        hoaDon.setSoTien(soTien);
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setHanThanhToan(LocalDate.now().plusDays(7));
        hoaDon.setTrangThai(AppConstants.HOA_DON_CHUA_THANH_TOAN);
        hoaDon.setMaNVTao(maNV);
        hoaDonRepository.save(hoaDon);
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
     * Lấy đơn đăng ký theo mã đơn.
     * 
     * @param maDon Mã đơn đăng ký.
     * @return DonDangKy.
     */
    public DonDangKy getDonDangKyById(Integer maDon) {
        return donDangKyRepository.findById(maDon)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đăng ký với mã: " + maDon));
    }
}
