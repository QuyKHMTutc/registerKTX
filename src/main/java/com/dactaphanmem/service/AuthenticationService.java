package com.dactaphanmem.service;

import com.dactaphanmem.dto.request.RegisterRequest;
import com.dactaphanmem.model.NhanVien;
import com.dactaphanmem.model.SinhVien;
import com.dactaphanmem.model.TaiKhoan;
import com.dactaphanmem.repository.NhanVienRepository;
import com.dactaphanmem.repository.SinhVienRepository;
import com.dactaphanmem.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final SinhVienRepository sinhVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // OTP cho việc kích hoạt tài khoản
    private final Map<String, String> activationOtpStorage = new ConcurrentHashMap<>();
    // OTP cho việc reset mật khẩu
    private final Map<String, String> passwordResetOtpStorage = new ConcurrentHashMap<>();

    public TaiKhoan getCurrentTaiKhoan() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Không có người dùng nào được xác thực.");
        }
        String username = authentication.getName();
        return taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Không tìm thấy tài khoản với tên đăng nhập: " + username));
    }

    public NhanVien getCurrentNhanVien() {
        TaiKhoan taiKhoan = getCurrentTaiKhoan();
        return nhanVienRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new IllegalStateException(
                        "Tài khoản hiện tại không được liên kết với bất kỳ nhân viên nào."));
    }

    public SinhVien getCurrentSinhVien() {
        TaiKhoan taiKhoan = getCurrentTaiKhoan();
        return sinhVienRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new IllegalStateException(
                        "Tài khoản hiện tại không được liên kết với bất kỳ sinh viên nào."));
    }

    // Phương thức hỗ trợ để lấy TaiKhoan theo email
    public Optional<TaiKhoan> getTaiKhoanByEmail(String email) {
        return taiKhoanRepository.findByEmail(email);
    }

    // Phương thức hỗ trợ để lấy TaiKhoan theo username
    public Optional<TaiKhoan> getTaiKhoanByUsername(String username) {
        return taiKhoanRepository.findByTenDangNhap(username);
    }

    @Transactional
    public TaiKhoan register(RegisterRequest request) {
        log.info("Bắt đầu đăng ký tài khoản cho username: {}", request.getUsername());

        if (taiKhoanRepository.existsByTenDangNhap(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        if (taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(request.getUsername());
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setLoaiTK("SV");
        taiKhoan.setTrangThai(1);
        taiKhoan.setEnabled(false);

        TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);
        log.info("Đã lưu tài khoản thành công. Mã TK: {}", savedTaiKhoan.getMaTK());

        SinhVien sinhVien = new SinhVien();
        sinhVien.setMaSV(request.getUsername());
        sinhVien.setTaiKhoan(savedTaiKhoan);
        sinhVienRepository.save(sinhVien);
        log.info("Đã tạo thông tin sinh viên với maSV: {}", request.getUsername());

        sendOtp(savedTaiKhoan.getTenDangNhap(), savedTaiKhoan.getEmail(), "activation");

        return savedTaiKhoan;
    }

    @Transactional
    public TaiKhoan verifyOtpAndActivateAccount(String username, String otp) {
        String storedOtp = activationOtpStorage.get(username);

        if (storedOtp == null) {
            throw new IllegalArgumentException("Mã OTP không tồn tại hoặc đã hết hạn. Vui lòng yêu cầu gửi lại.");
        }
        if (!storedOtp.equals(otp)) {
            throw new IllegalArgumentException("Mã OTP không chính xác. Vui lòng kiểm tra lại.");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));

        if (taiKhoan.isEnabled()) {
            throw new IllegalStateException("Tài khoản đã được kích hoạt.");
        }

        taiKhoan.setEnabled(true);
        taiKhoanRepository.save(taiKhoan);
        activationOtpStorage.remove(username);
        log.info("Tài khoản {} đã được kích hoạt thành công.", username);

        return taiKhoan;
    }

    public void resendActivationOtp(String username) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản để gửi lại OTP."));

        if (taiKhoan.isEnabled()) {
            throw new IllegalStateException("Tài khoản này đã được kích hoạt rồi.");
        }

        sendOtp(taiKhoan.getTenDangNhap(), taiKhoan.getEmail(), "activation");
    }

    public void generatePasswordResetToken(String email) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản nào với email này."));

        sendOtp(taiKhoan.getTenDangNhap(), email, "password-reset");
    }

    public void resetPassword(String username, String otp, String newPassword) {
        // Validation đầu vào
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (otp == null || otp.isBlank()) {
            throw new IllegalArgumentException("Mã OTP không được để trống.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống.");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }

        String storedOtp = passwordResetOtpStorage.get(username);

        if (storedOtp == null) {
            throw new IllegalArgumentException("Mã OTP không tồn tại hoặc đã hết hạn. Vui lòng yêu cầu gửi lại.");
        }
        if (!storedOtp.equals(otp)) {
            throw new IllegalArgumentException("Mã OTP không chính xác. Vui lòng kiểm tra lại.");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));

        taiKhoan.setMatKhau(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(taiKhoan);
        passwordResetOtpStorage.remove(username);
        log.info("Đã đặt lại mật khẩu thành công cho tài khoản {}", username);
    }

    private void sendOtp(String username, String email, String type) {
        String otp = generateOtp();
        String subject;
        String emailContent;

        if ("activation".equals(type)) {
            activationOtpStorage.put(username, otp);
            subject = "Xác thực tài khoản KTX - Mã OTP của bạn";
            emailContent = createEmailContent(otp, "để hoàn tất quá trình xác thực tài khoản của bạn");
        } else if ("password-reset".equals(type)) {
            passwordResetOtpStorage.put(username, otp);
            subject = "Đặt lại mật khẩu KTX - Mã OTP của bạn";
            emailContent = createEmailContent(otp, "để đặt lại mật khẩu của bạn");
        } else {
            throw new IllegalArgumentException("Loại OTP không hợp lệ.");
        }

        log.info("Tạo mã OTP mới ({}) : {} cho user: {}", type, otp, username);
        emailService.sendSimpleMessage(email, subject, emailContent);
        log.info("Đã gửi OTP ({}) đến email: {}", type, email);
    }

    private String createEmailContent(String otp, String purpose) {
        return "Xin chào bạn,\n\n"
                + "Mã xác thực OTP của bạn là: " + otp + "\n\n"
                + "Vui lòng sử dụng mã này " + purpose + ".\n"
                + "Mã OTP này sẽ hết hạn sau vài phút.\n\n"
                + "Trân trọng,\n"
                + "Ban quản lý KTX";
    }

    private String generateOtp() {
        return String.valueOf(100000 + new SecureRandom().nextInt(900000));
    }
}
