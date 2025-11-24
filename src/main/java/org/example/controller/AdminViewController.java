package org.example.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.dto.PhongForm;
import org.example.model.DonDangKy;
import org.example.model.NhanVien;
import org.example.model.ThongBao;
import org.example.model.YeuCauChonPhong;
import org.example.model.TaiKhoan;

import org.example.repository.LichSuDuyetDonRepository;
import org.example.repository.LichSuThanhToanRepository;
import org.example.repository.LoaiPhongRepository;
import org.example.repository.ToaNhaRepository;
import org.example.repository.YeuCauChonPhongRepository;
import org.example.service.AuthenticationService;
import org.example.service.DuyetDonService;
import org.example.service.HoaDonService;
import org.example.service.HopDongService;
import org.example.service.PhongService;
import org.example.service.TaiKhoanService;
import org.example.service.ThoiGianDangKyService;
import org.example.service.ThongBaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final PhongService phongService;
    private final DuyetDonService duyetDonService;
    private final LoaiPhongRepository loaiPhongRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final HopDongService hopDongService;
    private final HoaDonService hoaDonService;
    private final LichSuThanhToanRepository lichSuThanhToanRepository;
    private final LichSuDuyetDonRepository lichSuDuyetDonRepository;
    private final YeuCauChonPhongRepository yeuCauChonPhongRepository;
    private final AuthenticationService authService;
    private final TaiKhoanService taiKhoanService;
    private final ThoiGianDangKyService thoiGianDangKyService;
    private final ThongBaoService thongBaoService;

    public AdminViewController(PhongService phongService,
            DuyetDonService duyetDonService,
            LoaiPhongRepository loaiPhongRepository,
            ToaNhaRepository toaNhaRepository,
            HopDongService hopDongService,
            HoaDonService hoaDonService,
            LichSuThanhToanRepository lichSuThanhToanRepository,
            LichSuDuyetDonRepository lichSuDuyetDonRepository,
            YeuCauChonPhongRepository yeuCauChonPhongRepository,
            AuthenticationService authService,
            TaiKhoanService taiKhoanService,
            ThoiGianDangKyService thoiGianDangKyService,
            ThongBaoService thongBaoService) {
        this.phongService = phongService;
        this.duyetDonService = duyetDonService;
        this.loaiPhongRepository = loaiPhongRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.hopDongService = hopDongService;
        this.hoaDonService = hoaDonService;
        this.lichSuThanhToanRepository = lichSuThanhToanRepository;
        this.lichSuDuyetDonRepository = lichSuDuyetDonRepository;
        this.yeuCauChonPhongRepository = yeuCauChonPhongRepository;
        this.authService = authService;
        this.taiKhoanService = taiKhoanService;
        this.thoiGianDangKyService = thoiGianDangKyService;
        this.thongBaoService = thongBaoService;
    }

    /**
     * Add unread notification count to all admin pages
     * MOVED TO GlobalControllerAdvice
     */

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard() {
        return "redirect:/admin/phong";
    }

    @GetMapping("/phong")
    public String viewPhongs(Model model) {
        model.addAttribute("pageTitle", "Quản lý Phòng");
        model.addAttribute("phongs", phongService.getAllPhongs());
        model.addAttribute("phongForm", new PhongForm());
        model.addAttribute("loaiPhongs", loaiPhongRepository.findAll());
        model.addAttribute("toaNhas", toaNhaRepository.findAll());
        return "admin/phong";
    }

    @PostMapping("/phong")
    public String createPhong(@Valid @ModelAttribute PhongForm phongForm, RedirectAttributes redirectAttributes) {
        phongService.createPhong(phongForm);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo phòng thành công.");
        return "redirect:/admin/phong";
    }

    @PostMapping("/phong/{maPhong}/trangthai")
    public String updatePhongStatus(@PathVariable Integer maPhong, @RequestParam String trangThai,
            RedirectAttributes redirectAttributes) {
        phongService.updateTrangThai(maPhong, trangThai);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái phòng thành công.");
        return "redirect:/admin/phong";
    }

    @GetMapping("/don-dang-ky")
    public String viewPendingApplications(Model model) {
        model.addAttribute("pageTitle", "Duyệt Đơn Đăng Ký");
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
        NhanVien admin = authService.getCurrentNhanVien();
        DonDangKy donDangKy = duyetDonService.getDonDangKyById(maDon);

        if (maPhong != null) {
            duyetDonService.approveApplicationWithManualRoom(donDangKy, admin.getMaNV(), maPhong);
        } else {
            duyetDonService.approveApplicationAutomatically(donDangKy, admin.getMaNV());
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt đơn #" + maDon + " thành công.");
        return "redirect:/admin/don-dang-ky";
    }

    @PostMapping("/don-dang-ky/{maDon}/reject")
    public String rejectDon(@PathVariable Integer maDon, @RequestParam String lyDo,
            RedirectAttributes redirectAttributes) {
        NhanVien admin = authService.getCurrentNhanVien();
        DonDangKy donDangKy = duyetDonService.getDonDangKyById(maDon);
        duyetDonService.rejectApplication(donDangKy, admin.getMaNV(), lyDo);
        redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối đơn #" + maDon);
        return "redirect:/admin/don-dang-ky";
    }

    @GetMapping("/hop-dong")
    public String viewHopDong(Model model) {
        model.addAttribute("pageTitle", "Quản lý Hợp Đồng");
        model.addAttribute("hopDongs", hopDongService.getAllHopDongs());
        return "admin/hop-dong";
    }

    @GetMapping("/hoa-don")
    public String viewHoaDon(Model model) {
        model.addAttribute("pageTitle", "Quản lý Hóa Đơn");
        model.addAttribute("hoaDons", hoaDonService.getAllHoaDons());
        return "admin/hoa-don";
    }

    @GetMapping("/lich-su-thanh-toan")
    public String viewLichSuThanhToan(Model model) {
        model.addAttribute("pageTitle", "Lịch Sử Thanh Toán");
        model.addAttribute("lichSuThanhToans", lichSuThanhToanRepository.findAll());
        return "admin/lich-su-thanh-toan";
    }

    @GetMapping("/lich-su-duyet-don")
    public String viewLichSuDuyetDon(Model model) {
        model.addAttribute("pageTitle", "Lịch Sử Duyệt Đơn");
        model.addAttribute("lichSuDuyetDons", lichSuDuyetDonRepository.findAll());
        return "admin/lich-su-duyet-don";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        model.addAttribute("pageTitle", "Hồ Sơ");
        NhanVien nhanVien = authService.getCurrentNhanVien();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("taiKhoan", nhanVien.getTaiKhoan());
        return "admin/profile";
    }

    @GetMapping("/settings")
    public String viewSettings(Model model) {
        model.addAttribute("pageTitle", "Cài Đặt");
        return "admin/settings";
    }

    @GetMapping("/tai-khoan")
    public String viewTaiKhoan(Model model) {
        model.addAttribute("pageTitle", "Quản Lý Tài Khoản");
        List<TaiKhoan> taiKhoans = taiKhoanService.getTaiKhoanByLoai("SV");
        model.addAttribute("taiKhoans", taiKhoans);
        return "admin/tai-khoan";
    }

    @PostMapping("/tai-khoan/{maTK}/khoa")
    public String khoaTaiKhoan(@PathVariable Integer maTK, RedirectAttributes redirectAttributes) {
        taiKhoanService.khoaTaiKhoan(maTK);
        redirectAttributes.addFlashAttribute("successMessage",
                "Đã khóa tài khoản thành công.");
        return "redirect:/admin/tai-khoan";
    }

    @PostMapping("/tai-khoan/{maTK}/mo-khoa")
    public String moKhoaTaiKhoan(@PathVariable Integer maTK, RedirectAttributes redirectAttributes) {
        taiKhoanService.moKhoaTaiKhoan(maTK);
        redirectAttributes.addFlashAttribute("successMessage",
                "Đã mở khóa tài khoản thành công.");
        return "redirect:/admin/tai-khoan";
    }

    @GetMapping("/thoi-gian-dang-ky")
    public String viewThoiGianDangKy(Model model) {
        model.addAttribute("pageTitle", "Quản Lý Thời Gian Đăng Ký");
        model.addAttribute("periods", thoiGianDangKyService.getAllPeriods());
        return "admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky")
    public String createThoiGianDangKy(@RequestParam String ngayMo, @RequestParam String ngayDong,
            @RequestParam String moTa, RedirectAttributes redirectAttributes) {
        thoiGianDangKyService.createPeriod(
                java.time.LocalDateTime.parse(ngayMo),
                java.time.LocalDateTime.parse(ngayDong),
                moTa);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo đợt đăng ký thành công.");
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky/{maDot}/update")
    public String updateThoiGianDangKy(@PathVariable Integer maDot, @RequestParam String ngayMo,
            @RequestParam String ngayDong, @RequestParam String moTa,
            RedirectAttributes redirectAttributes) {
        thoiGianDangKyService.updatePeriod(
                maDot,
                java.time.LocalDateTime.parse(ngayMo),
                java.time.LocalDateTime.parse(ngayDong),
                moTa);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật đợt đăng ký thành công.");
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky/{maDot}/delete")
    public String deleteThoiGianDangKy(@PathVariable Integer maDot, RedirectAttributes redirectAttributes) {
        thoiGianDangKyService.deletePeriod(maDot);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa đợt đăng ký thành công.");
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @GetMapping("/thong-bao/unread")
    @ResponseBody
    public List<ThongBao> getUnreadNotifications() {
        return thongBaoService.getUnreadNotifications();
    }

    @PostMapping("/thong-bao/{id}/mark-read")
    @ResponseBody
    public Map<String, Object> markNotificationAsRead(@PathVariable Integer id) {
        try {
            thongBaoService.markAsRead(id);
            return Map.of("success", true, "unreadCount", thongBaoService.getUnreadCount());
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }
}
