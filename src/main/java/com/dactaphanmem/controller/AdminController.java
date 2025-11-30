package com.dactaphanmem.controller;

import com.dactaphanmem.dto.request.PhongForm;
import com.dactaphanmem.dto.response.*;
import com.dactaphanmem.mapper.EntityMapper;
import com.dactaphanmem.model.*;
import com.dactaphanmem.repository.*;
import com.dactaphanmem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PhongService phongService;
    private final DuyetDonService duyetDonService;
    private final AuthenticationService authService;
    private final ThoiGianDangKyService thoiGianDangKyService;
    private final ThongBaoService thongBaoService;
    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final LichSuDuyetDonRepository lichSuDuyetDonRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final EntityMapper mapper;

    @GetMapping("/phong")
    public ResponseEntity<ApiResponse<List<PhongResponse>>> getAllPhongs() {
        return ResponseEntity.ok(ApiResponse.success(phongService.getAllPhongs().stream()
                .map(mapper::toPhongResponse).collect(Collectors.toList())));
    }

    @PostMapping("/phong")
    public ResponseEntity<ApiResponse<PhongResponse>> createNewPhong(@Valid @RequestBody PhongForm phongForm) {
        return ResponseEntity.ok(ApiResponse.success("Tạo phòng thành công",
                mapper.toPhongResponse(phongService.createPhong(phongForm))));
    }

    @PutMapping("/phong/{maPhong}/trangthai")
    public ResponseEntity<ApiResponse<PhongResponse>> updatePhongTrangThai(@PathVariable Integer maPhong,
            @RequestParam String trangThai) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công",
                mapper.toPhongResponse(phongService.updateTrangThai(maPhong, trangThai))));
    }

    @GetMapping("/don-dang-ky/pending")
    public ResponseEntity<ApiResponse<List<DonDangKyResponse>>> getPendingApplications() {
        return ResponseEntity.ok(ApiResponse.success(duyetDonService.getPendingApplications().stream()
                .map(mapper::toDonDangKyResponse).collect(Collectors.toList())));
    }

    @GetMapping("/don-dang-ky/available-rooms")
    public ResponseEntity<ApiResponse<List<PhongResponse>>> getAvailableRooms(@RequestParam String maSV) {
        return ResponseEntity.ok(ApiResponse.success(duyetDonService.getAvailableRooms(maSV).stream()
                .map(mapper::toPhongResponse).collect(Collectors.toList())));
    }

    @PostMapping("/don-dang-ky/{maDon}/approve")
    public ResponseEntity<ApiResponse<String>> approveApplication(@PathVariable Integer maDon,
            @RequestParam(required = false) Integer maPhong) {
        NhanVien admin = authService.getCurrentNhanVien();
        DonDangKy don = duyetDonService.getDonDangKyById(maDon);
        if (maPhong != null) {
            duyetDonService.approveApplicationWithManualRoom(don, admin.getMaNV(), maPhong);
        } else {
            duyetDonService.approveApplicationAutomatically(don, admin.getMaNV());
        }
        return ResponseEntity.ok(ApiResponse.success("Duyệt đơn thành công", null));
    }

    @PostMapping("/don-dang-ky/{maDon}/reject")
    public ResponseEntity<ApiResponse<String>> rejectApplication(@PathVariable Integer maDon,
            @RequestParam String lyDo) {
        NhanVien admin = authService.getCurrentNhanVien();
        duyetDonService.rejectApplication(duyetDonService.getDonDangKyById(maDon), admin.getMaNV(), lyDo);
        return ResponseEntity.ok(ApiResponse.success("Từ chối đơn thành công", null));
    }

    @GetMapping("/hop-dong")
    public ResponseEntity<ApiResponse<List<HopDongResponse>>> getAllHopDongs() {
        return ResponseEntity.ok(ApiResponse.success(hopDongRepository.findAll().stream()
                .map(mapper::toHopDongResponse).collect(Collectors.toList())));
    }

    @GetMapping("/hoa-don")
    public ResponseEntity<ApiResponse<List<HoaDonResponse>>> getAllHoaDons() {
        return ResponseEntity.ok(ApiResponse.success(hoaDonRepository.findAll().stream()
                .map(mapper::toHoaDonResponse).collect(Collectors.toList())));
    }

    @GetMapping("/lich-su-thanh-toan")
    public ResponseEntity<ApiResponse<List<LichSuThanhToanResponse>>> getAllLichSuThanhToan() {
        return ResponseEntity.ok(ApiResponse.success(lichSuThanhToanRepository.findAll().stream()
                .map(mapper::toLichSuThanhToanResponse).collect(Collectors.toList())));
    }

    @GetMapping("/lich-su-duyet-don")
    public ResponseEntity<ApiResponse<List<LichSuDuyetDonResponse>>> getAllLichSuDuyetDon() {
        return ResponseEntity.ok(ApiResponse.success(lichSuDuyetDonRepository.findAll().stream()
                .map(mapper::toLichSuDuyetDonResponse).collect(Collectors.toList())));
    }

    @GetMapping("/tai-khoan")
    public ResponseEntity<ApiResponse<List<TaiKhoanResponse>>> getAllTaiKhoanSinhVien() {
        return ResponseEntity.ok(ApiResponse.success(taiKhoanRepository.findByLoaiTK("SV").stream()
                .map(mapper::toTaiKhoanResponse).collect(Collectors.toList())));
    }

    @PostMapping("/tai-khoan/{maTK}/khoa")
    public ResponseEntity<ApiResponse<String>> khoaTaiKhoan(@PathVariable Integer maTK) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        taiKhoan.setTrangThai(0);
        taiKhoanRepository.save(taiKhoan);
        return ResponseEntity.ok(ApiResponse.success("Khóa tài khoản thành công", null));
    }

    @PostMapping("/tai-khoan/{maTK}/mo-khoa")
    public ResponseEntity<ApiResponse<String>> moKhoaTaiKhoan(@PathVariable Integer maTK) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        taiKhoan.setTrangThai(1);
        taiKhoanRepository.save(taiKhoan);
        return ResponseEntity.ok(ApiResponse.success("Mở khóa tài khoản thành công", null));
    }

    @GetMapping("/thoi-gian-dang-ky")
    public ResponseEntity<ApiResponse<List<ThoiGianDangKyResponse>>> getAllThoiGianDangKy() {
        return ResponseEntity.ok(ApiResponse.success(thoiGianDangKyService.getAllPeriods().stream()
                .map(mapper::toThoiGianDangKyResponse).collect(Collectors.toList())));
    }

    @PostMapping("/thoi-gian-dang-ky")
    public ResponseEntity<ApiResponse<String>> createPeriod(
            @RequestParam("ngayMo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayMo,
            @RequestParam("ngayDong") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayDong,
            @RequestParam("moTa") String moTa) {
        thoiGianDangKyService.createPeriod(ngayMo, ngayDong, moTa);
        return ResponseEntity.ok(ApiResponse.success("Tạo đợt đăng ký thành công", null));
    }

    @PutMapping("/thoi-gian-dang-ky/{maDot}")
    public ResponseEntity<ApiResponse<String>> updatePeriod(@PathVariable Integer maDot,
            @RequestParam("ngayMo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayMo,
            @RequestParam("ngayDong") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayDong,
            @RequestParam("moTa") String moTa) {
        thoiGianDangKyService.updatePeriod(maDot, ngayMo, ngayDong, moTa);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đợt đăng ký thành công", null));
    }

    @DeleteMapping("/thoi-gian-dang-ky/{maDot}")
    public ResponseEntity<ApiResponse<String>> deletePeriod(@PathVariable Integer maDot) {
        thoiGianDangKyService.deletePeriod(maDot);
        return ResponseEntity.ok(ApiResponse.success("Xóa đợt đăng ký thành công", null));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<NhanVienResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success(mapper.toNhanVienResponse(authService.getCurrentNhanVien())));
    }

    @GetMapping("/thong-bao/{id}/read")
    public ResponseEntity<ApiResponse<String>> markNotificationAsRead(@PathVariable Integer id) {
        thongBaoService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu thông báo là đã đọc", null));
    }
}
