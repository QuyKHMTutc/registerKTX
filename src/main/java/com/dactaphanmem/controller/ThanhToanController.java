package com.dactaphanmem.controller;

import com.dactaphanmem.dto.ThanhToanRequest;
import com.dactaphanmem.model.NhanVien;
import com.dactaphanmem.service.AuthenticationService;
import com.dactaphanmem.service.ThanhToanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/thanh-toan")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;
    private final AuthenticationService authService;

    public ThanhToanController(ThanhToanService thanhToanService,
                              AuthenticationService authService) {
        this.thanhToanService = thanhToanService;
        this.authService = authService;
    }

    @PostMapping("/{maHoaDon}")
    public ResponseEntity<?> processPayment(
            @PathVariable Integer maHoaDon,
            @RequestBody ThanhToanRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer maNVXacNhan = null;
        // Nếu là admin thanh toán hộ, lấy mã NV.
        if (userDetails != null && userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            NhanVien admin = authService.getCurrentNhanVien();
            maNVXacNhan = admin.getMaNV();
        }
        
        var lichSuThanhToan = thanhToanService.thanhToanHoaDon(
                maHoaDon,
                request.getPhuongThuc(),
                maNVXacNhan 
        );
        return ResponseEntity.ok(lichSuThanhToan);
    }
}
