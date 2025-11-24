package com.dactaphanmem.service;

import com.dactaphanmem.model.TaiKhoan;
import com.dactaphanmem.repository.TaiKhoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminSetupService {

    private static final Logger logger = LoggerFactory.getLogger(AdminSetupService.class);

    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSetupService(TaiKhoanRepository taiKhoanRepository, PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createDefaultAdminUser() {
        String adminUsername = "admin";
        String adminPassword = "admin"; // Mật khẩu mặc định
        String adminEmail = "admin@example.com"; // Email mặc định cho admin

        Optional<TaiKhoan> existingAdmin = taiKhoanRepository.findByTenDangNhap(adminUsername);

        if (existingAdmin.isEmpty()) {
            logger.info("Tài khoản Admin mặc định không tồn tại. Đang tạo...");

            TaiKhoan adminAccount = new TaiKhoan();
            adminAccount.setTenDangNhap(adminUsername);
            adminAccount.setMatKhau(passwordEncoder.encode(adminPassword));
            adminAccount.setEmail(adminEmail);
            adminAccount.setLoaiTK("NV"); // "NV" (Nhân Viên) sẽ có ROLE_ADMIN
            adminAccount.setTrangThai(1); // Active
            adminAccount.setEnabled(true); // Đã kích hoạt

            taiKhoanRepository.save(adminAccount);
            logger.info("Tài khoản Admin mặc định đã được tạo thành công: username='{}'", adminUsername);
        } else {
            logger.info("Tài khoản Admin mặc định đã tồn tại.");
            // Tùy chọn: Bạn có thể cập nhật mật khẩu hoặc các thông tin khác ở đây nếu muốn
            // Ví dụ:
            // TaiKhoan adminAccount = existingAdmin.get();
            // if (!passwordEncoder.matches(adminPassword, adminAccount.getMatKhau())) {
            //     adminAccount.setMatKhau(passwordEncoder.encode(adminPassword));
            //     taiKhoanRepository.save(adminAccount);
            //     logger.info("Mật khẩu tài khoản Admin đã được cập nhật.");
            // }
        }
    }
}
