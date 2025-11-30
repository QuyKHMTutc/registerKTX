package com.dactaphanmem.service;

import com.dactaphanmem.model.TaiKhoan;
import com.dactaphanmem.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSetupService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createDefaultAdminUser() {
        String adminUsername = "admin";
        String adminPassword = "admin"; // Mật khẩu mặc định
        String adminEmail = "admin@example.com"; // Email mặc định cho admin

        Optional<TaiKhoan> existingAdmin = taiKhoanRepository.findByTenDangNhap(adminUsername);

        if (existingAdmin.isEmpty()) {
            log.info("Tài khoản Admin mặc định không tồn tại. Đang tạo...");

            TaiKhoan adminAccount = new TaiKhoan();
            adminAccount.setTenDangNhap(adminUsername);
            adminAccount.setMatKhau(passwordEncoder.encode(adminPassword));
            adminAccount.setEmail(adminEmail);
            adminAccount.setLoaiTK("NV"); // "NV" (Nhân Viên) sẽ có ROLE_ADMIN
            adminAccount.setTrangThai(1); // Active
            adminAccount.setEnabled(true); // Đã kích hoạt

            taiKhoanRepository.save(adminAccount);
            log.info("Tài khoản Admin mặc định đã được tạo thành công: username='{}'", adminUsername);
        } else {
            log.info("Tài khoản Admin mặc định đã tồn tại.");
            // Tùy chọn: Bạn có thể cập nhật mật khẩu hoặc các thông tin khác ở đây nếu muốn
            // Ví dụ:
            // TaiKhoan adminAccount = existingAdmin.get();
            // if (!passwordEncoder.matches(adminPassword, adminAccount.getMatKhau())) {
            // adminAccount.setMatKhau(passwordEncoder.encode(adminPassword));
            // taiKhoanRepository.save(adminAccount);
            // log.info("Mật khẩu tài khoản Admin đã được cập nhật.");
            // }
        }
    }
}
