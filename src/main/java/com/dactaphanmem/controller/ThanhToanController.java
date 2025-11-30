package com.dactaphanmem.controller;

import com.dactaphanmem.dto.request.ThanhToanRequest;
import com.dactaphanmem.dto.response.ApiResponse;
import com.dactaphanmem.dto.response.LichSuThanhToanResponse;
import com.dactaphanmem.mapper.EntityMapper;
import com.dactaphanmem.service.AuthenticationService;
import com.dactaphanmem.service.ThanhToanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/thanh-toan")
@RequiredArgsConstructor
public class ThanhToanController {

    private final ThanhToanService thanhToanService;
    private final AuthenticationService authService;
    private final EntityMapper mapper;

    @PostMapping("/{maHoaDon}")
    public ResponseEntity<ApiResponse<LichSuThanhToanResponse>> processPayment(
            @PathVariable Integer maHoaDon,
            @RequestBody ThanhToanRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer maNVXacNhan = null;
        if (userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            maNVXacNhan = authService.getCurrentNhanVien().getMaNV();
        }
        return ResponseEntity.ok(ApiResponse.success("Thanh toán thành công",
                mapper.toLichSuThanhToanResponse(
                        thanhToanService.thanhToanHoaDon(maHoaDon, request.getPhuongThuc(), maNVXacNhan))));
    }
}
