package com.dactaphanmem.security;

import com.dactaphanmem.model.TaiKhoan;
import com.dactaphanmem.repository.TaiKhoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        logger.info("Đang tìm tài khoản với tên đăng nhập: {}", tenDangNhap);
        
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> {
                    logger.warn("Không tìm thấy tài khoản với tên đăng nhập: {}", tenDangNhap);
                    return new UsernameNotFoundException("Tên đăng nhập hoặc mật khẩu không đúng.");
                });

        logger.info("Đã tìm thấy tài khoản. Mã TK: {}, Tên đăng nhập: {}, Loại TK: {}, Trạng thái: {}, Enabled: {}", 
                   taiKhoan.getMaTK(), taiKhoan.getTenDangNhap(), 
                   taiKhoan.getLoaiTK(), taiKhoan.getTrangThai(), taiKhoan.isEnabled());

        // KHÔNG ném exception ở đây.
        // Chỉ cần tạo đối tượng UserDetails với các cờ trạng thái chính xác.
        // DaoAuthenticationProvider sẽ tự động ném DisabledException hoặc LockedException.
        
        String matKhau = taiKhoan.getMatKhau();
        if (matKhau == null || matKhau.isEmpty()) {
            logger.error("Mật khẩu rỗng cho tài khoản: {}", tenDangNhap);
            throw new UsernameNotFoundException("Mật khẩu không hợp lệ cho tài khoản: " + tenDangNhap);
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        String loaiTK = taiKhoan.getLoaiTK();
        
        if (loaiTK == null || loaiTK.isEmpty()) {
            loaiTK = "SV";
        }
        
        switch (loaiTK.toUpperCase()) {
            case "NV":
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_NHAN_VIEN"));
                break;
            case "SV":
            default:
                authorities.add(new SimpleGrantedAuthority("ROLE_SINH_VIEN"));
                break;
        }

        logger.info("Loại tài khoản: {}, Authorities: {}", loaiTK, authorities);

        // Sử dụng các phương thức của builder để thiết lập trạng thái tài khoản.
        // Spring Security sẽ dựa vào đây để ném các exception phù hợp.
        return User.builder()
                .username(taiKhoan.getTenDangNhap())
                .password(taiKhoan.getMatKhau())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(taiKhoan.getTrangThai() != 1) // Sẽ ném LockedException nếu true
                .credentialsExpired(false)
                .disabled(!taiKhoan.isEnabled()) // Sẽ ném DisabledException nếu true
                .build();
    }
}
