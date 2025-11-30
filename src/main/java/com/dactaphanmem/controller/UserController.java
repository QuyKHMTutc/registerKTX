package com.dactaphanmem.controller;

import com.dactaphanmem.dto.request.DonDangKyForm;
import com.dactaphanmem.dto.request.SinhVienForm;
import com.dactaphanmem.dto.response.*;
import com.dactaphanmem.mapper.EntityMapper;
import com.dactaphanmem.model.*;
import com.dactaphanmem.repository.*;
import com.dactaphanmem.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final DonDangKyService donDangKyService;
    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final AuthenticationService authService;
    private final SinhVienService sinhVienService;
    private final EntityMapper mapper;

    @GetMapping("/check-info")
    public ResponseEntity<ApiResponse<Boolean>> checkInfo() {
        return ResponseEntity.ok(ApiResponse.success(sinhVienService.hasSinhVienInfo()));
    }

    @GetMapping("/thong-tin-sinh-vien")
    public ResponseEntity<ApiResponse<SinhVienResponse>> getThongTinSinhVien() {
        if (sinhVienService.hasSinhVienInfo()) {
            return ResponseEntity.ok(ApiResponse.success(mapper.toSinhVienResponse(authService.getCurrentSinhVien())));
        }
        TaiKhoan currentTaiKhoan = authService.getCurrentTaiKhoan();
        return ResponseEntity.ok(ApiResponse.success("Chưa có thông tin chi tiết",
                SinhVienResponse.builder().maSV(currentTaiKhoan.getTenDangNhap()).email(currentTaiKhoan.getEmail())
                        .build()));
    }

    @PostMapping("/thong-tin-sinh-vien")
    public ResponseEntity<ApiResponse<SinhVienResponse>> saveInitialSinhVienInfo(@RequestBody SinhVienForm form) {
        return ResponseEntity.ok(ApiResponse.success("Lưu thông tin thành công",
                mapper.toSinhVienResponse(sinhVienService.saveInitialSinhVienInfo(form))));
    }

    @PutMapping("/thong-tin-sinh-vien")
    public ResponseEntity<ApiResponse<SinhVienResponse>> updateThongTinSinhVien(@RequestBody SinhVien sinhVien) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công",
                mapper.toSinhVienResponse(sinhVienService.updateSinhVienInfo(sinhVien))));
    }

    @GetMapping("/don-dang-ky/open-dots")
    public ResponseEntity<ApiResponse<List<ThoiGianDangKyResponse>>> getOpenDots() {
        return ResponseEntity.ok(ApiResponse.success(donDangKyService.getOpenThoiGianDangKy().stream()
                .map(mapper::toThoiGianDangKyResponse).collect(Collectors.toList())));
    }

    @PostMapping("/don-dang-ky")
    public ResponseEntity<ApiResponse<DonDangKyResponse>> createDonDangKy(@RequestBody DonDangKyForm form) {
        SinhVien sinhVien = authService.getCurrentSinhVien();
        form.setMaSV(sinhVien.getMaSV());
        return ResponseEntity.ok(ApiResponse.success("Đơn đăng ký đã được gửi thành công",
                mapper.toDonDangKyResponse(donDangKyService.taoDonDangKy(form))));
    }

    @GetMapping("/don-dang-ky")
    public ResponseEntity<ApiResponse<List<DonDangKyResponse>>> myApplications() {
        return ResponseEntity.ok(
                ApiResponse.success(donDangKyService.getDonDangKyBySinhVien(authService.getCurrentSinhVien()).stream()
                        .map(mapper::toDonDangKyResponse).collect(Collectors.toList())));
    }

    @GetMapping("/hop-dong")
    public ResponseEntity<ApiResponse<List<HopDongResponse>>> myHopDong() {
        return ResponseEntity.ok(ApiResponse
                .success(hopDongRepository.findBySinhVien_MaSV(authService.getCurrentSinhVien().getMaSV()).stream()
                        .map(mapper::toHopDongResponse).collect(Collectors.toList())));
    }

    @GetMapping("/hoa-don")
    public ResponseEntity<ApiResponse<List<HoaDonResponse>>> myHoaDon() {
        return ResponseEntity.ok(ApiResponse
                .success(hoaDonRepository.findBySinhVien_MaSV(authService.getCurrentSinhVien().getMaSV()).stream()
                        .map(mapper::toHoaDonResponse).collect(Collectors.toList())));
    }

    @GetMapping("/lich-su-thanh-toan")
    public ResponseEntity<ApiResponse<List<LichSuThanhToanResponse>>> myLichSuThanhToan() {
        SinhVien sinhVien = authService.getCurrentSinhVien();
        List<HoaDon> hoaDons = hoaDonRepository.findBySinhVien_MaSV(sinhVien.getMaSV());
        return ResponseEntity.ok(ApiResponse.success(lichSuThanhToanRepository.findByHoaDonIn(hoaDons).stream()
                .map(mapper::toLichSuThanhToanResponse).collect(Collectors.toList())));
    }
}
