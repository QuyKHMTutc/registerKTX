package com.dactaphanmem.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class để tạo BCrypt hash cho mật khẩu
 * Chạy main method để generate hash cho "password123"
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hash = encoder.encode(password);
        
        System.out.println("=========================================");
        System.out.println("Mật khẩu: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("=========================================");
        System.out.println();
        System.out.println("SQL UPDATE statement:");
        System.out.println("UPDATE `tai_khoan` SET `mat_khau` = '" + hash + "' WHERE `mat_khau` = 'password123';");
    }
}

