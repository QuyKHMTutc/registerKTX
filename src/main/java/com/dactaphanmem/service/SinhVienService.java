package com.dactaphanmem.service;

import com.dactaphanmem.dto.request.SinhVienForm;
import com.dactaphanmem.model.SinhVien;
import com.dactaphanmem.repository.SinhVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SinhVienService {

    private final SinhVienRepository sinhVienRepository;
    private final AuthenticationService authService;

    /**
     * Cập nhật thông tin chi tiết cho sinh viên sau khi đăng ký.
     * Tên đăng nhập chính là Mã Sinh Viên.
     */
    @Transactional
    public SinhVien saveInitialSinhVienInfo(SinhVienForm form) {
        // Lấy sinh viên hiện tại, đã được tạo lúc đăng ký với maSV = tenDangNhap
        SinhVien currentSinhVien = authService.getCurrentSinhVien();

        // Kiểm tra xem maSV từ form có khớp với maSV của người dùng đang đăng nhập
        // không
        if (!currentSinhVien.getMaSV().equals(form.getMaSV())) {
            throw new IllegalArgumentException("Bạn không được phép thay đổi Mã sinh viên.");
        }

        // Cập nhật các thông tin còn thiếu từ form
        currentSinhVien.setHoTen(form.getHoTen());
        currentSinhVien.setLop(form.getLop());
        currentSinhVien.setKhoa(form.getKhoa());
        currentSinhVien.setSdt(form.getSdt());
        currentSinhVien.setGioiTinh(form.getGioiTinh());
        currentSinhVien.setNgaySinh(form.getNgaySinh());
        currentSinhVien.setDiaChi(form.getDiaChi());

        return sinhVienRepository.save(currentSinhVien);
    }

    /**
     * Cập nhật thông tin của sinh viên đã có.
     */
    @Transactional
    public SinhVien updateSinhVienInfo(SinhVien updatedInfo) {
        SinhVien currentSinhVien = authService.getCurrentSinhVien();

        // Cập nhật các trường có thể thay đổi
        currentSinhVien.setHoTen(updatedInfo.getHoTen());
        currentSinhVien.setNgaySinh(updatedInfo.getNgaySinh());
        currentSinhVien.setGioiTinh(updatedInfo.getGioiTinh());
        currentSinhVien.setDiaChi(updatedInfo.getDiaChi());
        currentSinhVien.setSdt(updatedInfo.getSdt());
        currentSinhVien.setLop(updatedInfo.getLop());
        currentSinhVien.setKhoa(updatedInfo.getKhoa());

        return sinhVienRepository.save(currentSinhVien);
    }

    /**
     * Kiểm tra xem sinh viên đã điền thông tin cơ bản (Họ tên) chưa.
     */
    public boolean hasSinhVienInfo() {
        try {
            SinhVien sv = authService.getCurrentSinhVien();
            // Coi là đã có thông tin nếu có Họ Tên
            return sv != null && sv.getHoTen() != null && !sv.getHoTen().isBlank();
        } catch (IllegalStateException e) {
            // Nếu không tìm thấy sinh viên (do lỗi nào đó), coi như chưa có thông tin
            return false;
        }
    }
}
