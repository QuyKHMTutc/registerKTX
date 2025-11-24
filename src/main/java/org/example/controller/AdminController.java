package org.example.controller;

import org.example.dto.PhongForm;
import org.example.model.DonDangKy;
import org.example.model.NhanVien;
import org.example.model.Phong;
import org.example.service.AuthenticationService;
import org.example.service.DuyetDonService;
import org.example.service.PhongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PhongService phongService;
    private final DuyetDonService duyetDonService;
    private final AuthenticationService authService;

    public AdminController(PhongService phongService,
                          DuyetDonService duyetDonService,
                          AuthenticationService authService) {
        this.phongService = phongService;
        this.duyetDonService = duyetDonService;
        this.authService = authService;
    }

    @GetMapping("/phong")
    public ResponseEntity<List<Phong>> getAllPhongs() {
        return ResponseEntity.ok(phongService.getAllPhongs());
    }

    @PostMapping("/phong")
    public ResponseEntity<?> createNewPhong(@RequestBody PhongForm phongForm) {
        try {
            Phong createdPhong = phongService.createPhong(phongForm);
            return ResponseEntity.ok(createdPhong);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/phong/{maPhong}/trangthai")
    public ResponseEntity<?> updatePhongTrangThai(@PathVariable Integer maPhong, @RequestParam String trangThai) {
        try {
            Phong updatedPhong = phongService.updateTrangThai(maPhong, trangThai);
            return ResponseEntity.ok(updatedPhong);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/don-dang-ky/pending")
    public ResponseEntity<?> getPendingApplications() {
        return ResponseEntity.ok(duyetDonService.getPendingApplications());
    }

    @GetMapping("/don-dang-ky/available-rooms")
    public ResponseEntity<?> getAvailableRooms(@RequestParam String maSV) {
        try {
            return ResponseEntity.ok(duyetDonService.getAvailableRooms(maSV));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/don-dang-ky/{maDon}/approve")
    public ResponseEntity<?> approveApplication(@PathVariable Integer maDon,
                                                @RequestParam(required = false) Integer maPhong) {
        try {
            NhanVien admin = authService.getCurrentNhanVien();
            DonDangKy don = duyetDonService.getDonDangKyById(maDon);

            if (maPhong != null) {
                return ResponseEntity.ok(duyetDonService.approveApplicationWithManualRoom(don, admin.getMaNV(), maPhong));
            } else {
                return ResponseEntity.ok(duyetDonService.approveApplicationAutomatically(don, admin.getMaNV()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PostMapping("/don-dang-ky/{maDon}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Integer maDon,
                                               @RequestParam String lyDo) {
        try {
            NhanVien admin = authService.getCurrentNhanVien();
            DonDangKy don = duyetDonService.getDonDangKyById(maDon);
            return ResponseEntity.ok(duyetDonService.rejectApplication(don, admin.getMaNV(), lyDo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}
