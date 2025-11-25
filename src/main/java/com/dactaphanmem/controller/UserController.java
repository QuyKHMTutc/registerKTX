package com.dactaphanmem.controller;

import java.util.List;
import com.dactaphanmem.dto.DonDangKyForm;
import com.dactaphanmem.dto.SinhVienForm;
import com.dactaphanmem.model.DonDangKy;
import com.dactaphanmem.model.HoaDon;
import com.dactaphanmem.model.LichSuThanhToan;
import com.dactaphanmem.model.SinhVien;
import com.dactaphanmem.model.TaiKhoan;
import com.dactaphanmem.model.ThoiGianDangKy;
import com.dactaphanmem.repository.HoaDonRepository;
import com.dactaphanmem.repository.HopDongRepository;
import com.dactaphanmem.repository.LichSuThanhToanRepository;
import com.dactaphanmem.service.AuthenticationService;
import com.dactaphanmem.service.DonDangKyService;
import com.dactaphanmem.service.SinhVienService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final DonDangKyService donDangKyService;
    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final AuthenticationService authService;
    private final SinhVienService sinhVienService;

    public UserController(DonDangKyService donDangKyService,
                         HopDongRepository hopDongRepository,
                         HoaDonRepository hoaDonRepository,
                         LichSuThanhToanRepository lichSuThanhToanRepository,
                         AuthenticationService authService,
                         SinhVienService sinhVienService) {
        this.donDangKyService = donDangKyService;
        this.hopDongRepository = hopDongRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.lichSuThanhToanRepository = lichSuThanhToanRepository;
        this.authService = authService;
        this.sinhVienService = sinhVienService;
    }

    // === KIỂM TRA THÔNG TIN SINH VIÊN ===
    /**
     * Kiểm tra xem sinh viên đã điền thông tin chưa
     * Nếu chưa thì redirect đến form điền thông tin
     * Nếu rồi thì redirect đến trang chủ
     */
    @GetMapping("/check-info")
    public String checkInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("UserController.checkInfo - User: {}, Authorities: {}", 
                   auth != null ? auth.getName() : "null",
                   auth != null ? auth.getAuthorities() : "null");
        
        try {
            boolean hasInfo = sinhVienService.hasSinhVienInfo();
            logger.info("Sinh viên đã có thông tin: {}", hasInfo);
            
            if (hasInfo) {
                return "redirect:/user/don-dang-ky/my-applications";
            } else {
                return "redirect:/user/thong-tin-sinh-vien";
            }
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra thông tin sinh viên: ", e);
            return "redirect:/user/thong-tin-sinh-vien";
        }
    }

    // === QUẢN LÝ THÔNG TIN SINH VIÊN ===
    @GetMapping("/thong-tin-sinh-vien")
    public String showThongTinSinhVien(Model model) {
        if (sinhVienService.hasSinhVienInfo()) {
            // Nếu đã có thông tin, chuyển đến trang cập nhật
            SinhVien currentSinhVien = authService.getCurrentSinhVien();
            model.addAttribute("sinhVien", currentSinhVien);
            model.addAttribute("taiKhoan", currentSinhVien.getTaiKhoan());
            return "user/thong-tin-sinh-vien-update";
        } else {
            // Nếu chưa có, hiển thị form điền thông tin ban đầu
            TaiKhoan currentTaiKhoan = authService.getCurrentTaiKhoan();
            SinhVienForm sinhVienForm = new SinhVienForm();
            sinhVienForm.setMaSV(currentTaiKhoan.getTenDangNhap());
            
            model.addAttribute("sinhVienForm", sinhVienForm);
            model.addAttribute("email", currentTaiKhoan.getEmail()); // Gửi email ra riêng để hiển thị
            return "user/thong-tin-sinh-vien";
        }
    }

    @PostMapping("/thong-tin-sinh-vien")
    public String saveInitialSinhVienInfo(@ModelAttribute SinhVienForm form, RedirectAttributes redirectAttributes) {
        try {
            sinhVienService.saveInitialSinhVienInfo(form);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin ban đầu thành công!");
            return "redirect:/user/don-dang-ky/my-applications";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/thong-tin-sinh-vien";
        }
    }

    @PostMapping("/thong-tin-sinh-vien/update")
    public String updateThongTinSinhVien(@ModelAttribute SinhVien sinhVien, RedirectAttributes redirectAttributes) {
        try {
            sinhVienService.updateSinhVienInfo(sinhVien);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/user/thong-tin-sinh-vien";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/thong-tin-sinh-vien-update"; // Stay on update page to show error
        }
    }

    // === QUẢN LÝ ĐƠN ĐĂNG KÝ ===
    @GetMapping("/don-dang-ky/create")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes) {
        if (!sinhVienService.hasSinhVienInfo()) {
             redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng cập nhật đầy đủ thông tin cá nhân trước khi tạo đơn.");
             return "redirect:/user/thong-tin-sinh-vien";
        }
        SinhVien sinhVien = authService.getCurrentSinhVien();
        DonDangKyForm form = new DonDangKyForm();
        form.setMaSV(sinhVien.getMaSV());
        model.addAttribute("donDangKyForm", form);
        model.addAttribute("sinhVien", sinhVien);

        List<ThoiGianDangKy> openDots = donDangKyService.getOpenThoiGianDangKy();
        model.addAttribute("openDots", openDots);

        if (openDots.isEmpty()) {
            model.addAttribute("noOpenDot", true);
        }
        return "user/create-don-dang-ky";
    }

    @PostMapping("/don-dang-ky/create")
    public String createDonDangKy(@ModelAttribute DonDangKyForm form, RedirectAttributes redirectAttributes) {
        SinhVien sinhVien = authService.getCurrentSinhVien();
        form.setMaSV(sinhVien.getMaSV());
        
        DonDangKy savedDon = donDangKyService.taoDonDangKy(form);
        redirectAttributes.addFlashAttribute("successMessage", "Đơn đăng ký đã được gửi thành công! Mã đơn: " + savedDon.getMaDon());
        return "redirect:/user/don-dang-ky/my-applications";
    }

    @GetMapping("/don-dang-ky/my-applications")
    public String myApplications(Model model) {
        SinhVien sinhVien = authService.getCurrentSinhVien();
        model.addAttribute("sinhVien", sinhVien);
        model.addAttribute("applications", donDangKyService.getDonDangKyBySinhVien(sinhVien));
        return "user/my-applications";
    }

    // === XEM HỢP ĐỒNG, HÓA ĐƠN ===
    @GetMapping("/hop-dong")
    public String myHopDong(Model model) {
        SinhVien sinhVien = authService.getCurrentSinhVien();
        model.addAttribute("sinhVien", sinhVien);
        model.addAttribute("hopDongs", hopDongRepository.findBySinhVien_MaSV(sinhVien.getMaSV()));
        return "user/hop-dong";
    }

    @GetMapping("/hoa-don")
    public String myHoaDon(Model model) {
        SinhVien sinhVien = authService.getCurrentSinhVien();
        model.addAttribute("sinhVien", sinhVien);
        model.addAttribute("hoaDons", hoaDonRepository.findBySinhVien_MaSV(sinhVien.getMaSV()));
        return "user/hoa-don";
    }

    @GetMapping("/lich-su-thanh-toan")
    public String myLichSuThanhToan(Model model) {
        SinhVien sinhVien = authService.getCurrentSinhVien();
        model.addAttribute("sinhVien", sinhVien);
        
        List<HoaDon> hoaDons = hoaDonRepository.findBySinhVien_MaSV(sinhVien.getMaSV());
        
        List<LichSuThanhToan> lichSuThanhToans = lichSuThanhToanRepository.findByHoaDonIn(hoaDons);
        model.addAttribute("lichSuThanhToans", lichSuThanhToans);
        
        return "user/lich-su-thanh-toan";
    }
}
