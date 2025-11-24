package com.dactaphanmem.controller;

import jakarta.validation.Valid;
import com.dactaphanmem.dto.PhongForm;
import com.dactaphanmem.model.DonDangKy;
import com.dactaphanmem.model.NhanVien;
import com.dactaphanmem.model.Phong;
import com.dactaphanmem.service.AuthenticationService;
import com.dactaphanmem.service.DuyetDonService;
import com.dactaphanmem.service.PhongService;
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
    public ResponseEntity<?> createNewPhong(@Valid @RequestBody PhongForm phongForm) {
        Phong createdPhong = phongService.createPhong(phongForm);
        return ResponseEntity.ok(createdPhong);
    }

    @PutMapping("/phong/{maPhong}/trangthai")
    public ResponseEntity<?> updatePhongTrangThai(@PathVariable Integer maPhong, @RequestParam String trangThai) {
        Phong updatedPhong = phongService.updateTrangThai(maPhong, trangThai);
        return ResponseEntity.ok(updatedPhong);
    }

    @GetMapping("/don-dang-ky/pending")
    public ResponseEntity<?> getPendingApplications() {
        return ResponseEntity.ok(duyetDonService.getPendingApplications());
    }

    @GetMapping("/don-dang-ky/available-rooms")
    public ResponseEntity<?> getAvailableRooms(@RequestParam String maSV) {
        return ResponseEntity.ok(duyetDonService.getAvailableRooms(maSV));
    }

    @PostMapping("/don-dang-ky/{maDon}/approve")
    public ResponseEntity<?> approveApplication(@PathVariable Integer maDon,
            @RequestParam(required = false) Integer maPhong) {
        NhanVien admin = authService.getCurrentNhanVien();
        DonDangKy don = duyetDonService.getDonDangKyById(maDon);

        if (maPhong != null) {
            return ResponseEntity.ok(duyetDonService.approveApplicationWithManualRoom(don, admin.getMaNV(), maPhong));
        } else {
            return ResponseEntity.ok(duyetDonService.approveApplicationAutomatically(don, admin.getMaNV()));
        }
    }

    @PostMapping("/don-dang-ky/{maDon}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Integer maDon,
            @RequestParam String lyDo) {
        NhanVien admin = authService.getCurrentNhanVien();
        DonDangKy don = duyetDonService.getDonDangKyById(maDon);
        return ResponseEntity.ok(duyetDonService.rejectApplication(don, admin.getMaNV(), lyDo));
    }
}
