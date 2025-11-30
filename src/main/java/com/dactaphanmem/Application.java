package com.dactaphanmem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.scheduling.annotation.EnableScheduling; // Removed

@SpringBootApplication
// @EnableScheduling // Removed
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner initAdminAccount(
            com.dactaphanmem.repository.TaiKhoanRepository taiKhoanRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            String username = "admin";
            String rawPassword = "123456";
            String email = "admin@example.com";

            java.util.Optional<com.dactaphanmem.model.TaiKhoan> adminOpt = taiKhoanRepository
                    .findByTenDangNhap(username);

            if (adminOpt.isPresent()) {
                com.dactaphanmem.model.TaiKhoan admin = adminOpt.get();
                String encodedPassword = passwordEncoder.encode(rawPassword);
                admin.setMatKhau(encodedPassword);
                taiKhoanRepository.save(admin);
                System.out.println("Admin password reset to: " + rawPassword);
            } else {
                com.dactaphanmem.model.TaiKhoan newAdmin = new com.dactaphanmem.model.TaiKhoan();
                newAdmin.setTenDangNhap(username);
                newAdmin.setMatKhau(passwordEncoder.encode(rawPassword));
                newAdmin.setEmail(email);
                newAdmin.setLoaiTK("NV");
                newAdmin.setTrangThai(1);
                newAdmin.setEnabled(true);
                taiKhoanRepository.save(newAdmin);
                System.out.println("Created default admin account: " + username + " / " + rawPassword);
            }
        };
    }
}
