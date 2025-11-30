package com.dactaphanmem.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.dactaphanmem.dto.request.PhongForm;
import com.dactaphanmem.model.DonDangKy;
import com.dactaphanmem.model.NhanVien;
import com.dactaphanmem.model.YeuCauChonPhong;
import com.dactaphanmem.model.TaiKhoan;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import com.dactaphanmem.repository.HoaDonRepository;
import com.dactaphanmem.repository.HopDongRepository;
import com.dactaphanmem.repository.LichSuDuyetDonRepository;
import com.dactaphanmem.repository.LichSuThanhToanRepository;
import com.dactaphanmem.repository.LoaiPhongRepository;
import com.dactaphanmem.repository.TaiKhoanRepository;
import com.dactaphanmem.repository.ToaNhaRepository;
import com.dactaphanmem.repository.YeuCauChonPhongRepository;
import com.dactaphanmem.service.AuthenticationService;
import com.dactaphanmem.service.DuyetDonService;
import com.dactaphanmem.service.PhongService;
import com.dactaphanmem.service.ThoiGianDangKyService;
import com.dactaphanmem.service.ThongBaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final PhongService phongService;
    private final DuyetDonService duyetDonService;
    private final LoaiPhongRepository loaiPhongRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final LichSuDuyetDonRepository lichSuDuyetDonRepository;
    private final YeuCauChonPhongRepository yeuCauChonPhongRepository;
    private final AuthenticationService authService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final ThoiGianDangKyService thoiGianDangKyService;
    private final ThongBaoService thongBaoService;
    private final org.springframework.context.MessageSource messageSource;

    public AdminViewController(PhongService phongService,
            DuyetDonService duyetDonService,
            LoaiPhongRepository loaiPhongRepository,
            ToaNhaRepository toaNhaRepository,
            HopDongRepository hopDongRepository,
            HoaDonRepository hoaDonRepository,
            LichSuThanhToanRepository lichSuThanhToanRepository,
            LichSuDuyetDonRepository lichSuDuyetDonRepository,
            YeuCauChonPhongRepository yeuCauChonPhongRepository,
            AuthenticationService authService,
            TaiKhoanRepository taiKhoanRepository,
            ThoiGianDangKyService thoiGianDangKyService,
            ThongBaoService thongBaoService,
            org.springframework.context.MessageSource messageSource) {
        this.phongService = phongService;
        this.duyetDonService = duyetDonService;
        this.loaiPhongRepository = loaiPhongRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.hopDongRepository = hopDongRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.lichSuThanhToanRepository = lichSuThanhToanRepository;
        this.lichSuDuyetDonRepository = lichSuDuyetDonRepository;
        this.yeuCauChonPhongRepository = yeuCauChonPhongRepository;
        this.authService = authService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.thoiGianDangKyService = thoiGianDangKyService;
        this.thongBaoService = thongBaoService;
        this.messageSource = messageSource;
    }

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard() {
        return "redirect:/admin/phong";
    }

    @GetMapping("/phong")
    public String viewPhongs(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.phong.title", null, java.util.Locale.getDefault()));
        model.addAttribute("phongs", phongService.getAllPhongs());
        model.addAttribute("phongForm", new PhongForm());
        model.addAttribute("loaiPhongs", loaiPhongRepository.findAll());
        model.addAttribute("toaNhas", toaNhaRepository.findAll());
        return "admin/phong";
    }

    @PostMapping("/phong")
    public String createPhong(@Valid @ModelAttribute PhongForm phongForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        // If validation fails, return to form with errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle",
                    messageSource.getMessage("page.phong.title", null, Locale.getDefault()));
            model.addAttribute("phongs", phongService.getAllPhongs());
            model.addAttribute("loaiPhongs", loaiPhongRepository.findAll());
            model.addAttribute("toaNhas", toaNhaRepository.findAll());
            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập vào.");
            return "admin/phong";
        }

        try {
            phongService.createPhong(phongForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("phong.create.success", null, Locale.getDefault()));
            return "redirect:/admin/phong";
        } catch (IllegalArgumentException e) {
            // Return to form with error message instead of redirect
            model.addAttribute("pageTitle",
                    messageSource.getMessage("page.phong.title", null, Locale.getDefault()));
            model.addAttribute("phongs", phongService.getAllPhongs());
            model.addAttribute("loaiPhongs", loaiPhongRepository.findAll());
            model.addAttribute("toaNhas", toaNhaRepository.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/phong";
        } catch (Exception e) {
            // Return to form with error message instead of redirect
            model.addAttribute("pageTitle",
                    messageSource.getMessage("page.phong.title", null, Locale.getDefault()));
            model.addAttribute("phongs", phongService.getAllPhongs());
            model.addAttribute("loaiPhongs", loaiPhongRepository.findAll());
            model.addAttribute("toaNhas", toaNhaRepository.findAll());
            model.addAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "admin/phong";
        }
    }

    @PostMapping("/phong/{maPhong}/trangthai")
    public String updatePhongStatus(@PathVariable Integer maPhong, @RequestParam String trangThai,
            RedirectAttributes redirectAttributes) {
        try {
            phongService.updateTrangThai(maPhong, trangThai);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("phong.update.status.success", null, java.util.Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/phong";
    }

    @GetMapping("/don-dang-ky")
    public String viewPendingApplications(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.dondangky.title", null, java.util.Locale.getDefault()));
        model.addAttribute("admin", authService.getCurrentNhanVien());
        List<DonDangKy> pendingApplications = duyetDonService.getPendingApplications();
        model.addAttribute("pendingApplications", pendingApplications);

        Map<Integer, YeuCauChonPhong> yeuCauMap = new HashMap<>();
        for (DonDangKy don : pendingApplications) {
            yeuCauChonPhongRepository.findByDonDangKy(don)
                    .ifPresent(yeuCau -> yeuCauMap.put(don.getMaDon(), yeuCau));
        }
        model.addAttribute("yeuCauMap", yeuCauMap);

        return "admin/don-dang-ky";
    }

    @PostMapping("/don-dang-ky/{maDon}/approve")
    public String approveDon(@PathVariable Integer maDon, @RequestParam(required = false) Integer maPhong,
            RedirectAttributes redirectAttributes) {
        try {
            NhanVien admin = authService.getCurrentNhanVien();
            DonDangKy donDangKy = duyetDonService.getDonDangKyById(maDon);

            if (maPhong != null) {
                duyetDonService.approveApplicationWithManualRoom(donDangKy, admin.getMaNV(), maPhong);
            } else {
                duyetDonService.approveApplicationAutomatically(donDangKy, admin.getMaNV());
            }
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("dondangky.approve.success",
                    new Object[] { maDon }, java.util.Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("error.system",
                    new Object[] { e.getMessage() }, java.util.Locale.getDefault()));
        }
        return "redirect:/admin/don-dang-ky";
    }

    @PostMapping("/don-dang-ky/{maDon}/reject")
    public String rejectDon(@PathVariable Integer maDon, @RequestParam String lyDo,
            RedirectAttributes redirectAttributes) {
        try {
            NhanVien admin = authService.getCurrentNhanVien();
            DonDangKy donDangKy = duyetDonService.getDonDangKyById(maDon);
            duyetDonService.rejectApplication(donDangKy, admin.getMaNV(), lyDo);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("dondangky.reject.success",
                    new Object[] { maDon }, java.util.Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("error.system",
                    new Object[] { e.getMessage() }, java.util.Locale.getDefault()));
        }
        return "redirect:/admin/don-dang-ky";
    }

    @GetMapping("/hop-dong")
    public String viewHopDong(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.hopdong.title", null, java.util.Locale.getDefault()));
        model.addAttribute("hopDongs", hopDongRepository.findAll());
        return "admin/hop-dong";
    }

    @GetMapping("/hoa-don")
    public String viewHoaDon(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.hoadon.title", null, java.util.Locale.getDefault()));
        model.addAttribute("hoaDons", hoaDonRepository.findAll());
        return "admin/hoa-don";
    }

    @GetMapping("/lich-su-thanh-toan")
    public String viewLichSuThanhToan(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.lichsuthanhtoan.title", null, java.util.Locale.getDefault()));
        model.addAttribute("lichSuThanhToans", lichSuThanhToanRepository.findAll());
        return "admin/lich-su-thanh-toan";
    }

    @GetMapping("/lich-su-duyet-don")
    public String viewLichSuDuyetDon(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.lichsuduyetdon.title", null, java.util.Locale.getDefault()));
        model.addAttribute("lichSuDuyetDons", lichSuDuyetDonRepository.findAll());
        return "admin/lich-su-duyet-don";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.profile.title", null, java.util.Locale.getDefault()));
        NhanVien nhanVien = authService.getCurrentNhanVien();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("taiKhoan", nhanVien.getTaiKhoan());
        return "admin/profile";
    }

    @GetMapping("/settings")
    public String viewSettings(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.settings.title", null, java.util.Locale.getDefault()));
        return "admin/settings";
    }

    @GetMapping("/tai-khoan")
    public String viewTaiKhoan(Model model) {
        model.addAttribute("pageTitle",
                messageSource.getMessage("page.taikhoan.title", null, java.util.Locale.getDefault()));
        List<TaiKhoan> taiKhoans = taiKhoanRepository.findByLoaiTK("SV");
        model.addAttribute("taiKhoans", taiKhoans);
        return "admin/tai-khoan";
    }

    @PostMapping("/tai-khoan/{maTK}/khoa")
    public String khoaTaiKhoan(@PathVariable Integer maTK, RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                    .orElseThrow(() -> new IllegalArgumentException(messageSource.getMessage("taikhoan.notfound",
                            new Object[] { maTK }, java.util.Locale.getDefault())));

            taiKhoan.setTrangThai(0);
            taiKhoanRepository.save(taiKhoan);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("taikhoan.lock.success",
                    new Object[] { taiKhoan.getTenDangNhap() }, java.util.Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("taikhoan.lock.error",
                    new Object[] { e.getMessage() }, java.util.Locale.getDefault()));
        }
        return "redirect:/admin/tai-khoan";
    }

    @PostMapping("/tai-khoan/{maTK}/mo-khoa")
    public String moKhoaTaiKhoan(@PathVariable Integer maTK, RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                    .orElseThrow(() -> new IllegalArgumentException(messageSource.getMessage("taikhoan.notfound",
                            new Object[] { maTK }, java.util.Locale.getDefault())));

            taiKhoan.setTrangThai(1);
            taiKhoanRepository.save(taiKhoan);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("taikhoan.unlock.success",
                    new Object[] { taiKhoan.getTenDangNhap() }, java.util.Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("taikhoan.unlock.error",
                    new Object[] { e.getMessage() }, java.util.Locale.getDefault()));
        }
        return "redirect:/admin/tai-khoan";
    }

    @GetMapping("/thoi-gian-dang-ky")
    public String thoiGianDangKyPage(Model model) {
        model.addAttribute("pageTitle", "Quản lý Thời Gian Đăng Ký");
        model.addAttribute("periods", thoiGianDangKyService.getAllPeriods());
        return "admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky")
    public String createPeriod(
            @RequestParam("ngayMo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayMo,
            @RequestParam("ngayDong") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayDong,
            @RequestParam("moTa") String moTa,
            RedirectAttributes redirectAttributes) {
        try {
            thoiGianDangKyService.createPeriod(ngayMo, ngayDong, moTa);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo đợt đăng ký thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky/{maDot}/update")
    public String updatePeriod(@PathVariable Integer maDot,
            @RequestParam("ngayMo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayMo,
            @RequestParam("ngayDong") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayDong,
            @RequestParam("moTa") String moTa,
            RedirectAttributes redirectAttributes) {
        try {
            thoiGianDangKyService.updatePeriod(maDot, ngayMo, ngayDong, moTa);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật đợt đăng ký thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky/{maDot}/delete")
    public String deletePeriod(@PathVariable Integer maDot, RedirectAttributes redirectAttributes) {
        try {
            thoiGianDangKyService.deletePeriod(maDot);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đợt đăng ký thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @GetMapping("/thong-bao/{id}/read")
    public String markNotificationAsRead(@PathVariable Integer id) {
        try {
            this.thongBaoService.markAsRead(id);

            // Redirect to the relevant page (currently all go to don-dang-ky)
            // In a more advanced version, we could inspect the notification type to
            // determine the redirect URL
            return "redirect:/admin/don-dang-ky";
        } catch (Exception e) {
            // If error, still redirect to safe page
            return "redirect:/admin/dashboard";
        }
    }
}
