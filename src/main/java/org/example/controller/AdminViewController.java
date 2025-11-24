package org.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.dto.PhongForm;
import org.example.model.DonDangKy;
import org.example.model.NhanVien;
import org.example.model.YeuCauChonPhong;
import org.example.model.TaiKhoan;
import org.example.repository.DonDangKyRepository;
import org.example.repository.HoaDonRepository;
import org.example.repository.HopDongRepository;
import org.example.repository.LichSuDuyetDonRepository;
import org.example.repository.LichSuThanhToanRepository;
import org.example.repository.LoaiPhongRepository;
import org.example.repository.TaiKhoanRepository;
import org.example.repository.ToaNhaRepository;
import org.example.repository.YeuCauChonPhongRepository;
import org.example.service.AuthenticationService;
import org.example.service.DuyetDonService;
import org.example.service.PhongService;
import org.example.service.ThoiGianDangKyService;
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
            ThoiGianDangKyService thoiGianDangKyService) {
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
    }

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
    public String createPhong(@ModelAttribute PhongForm phongForm, RedirectAttributes redirectAttributes) {
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
        try {
            NhanVien admin = authService.getCurrentNhanVien();
            DonDangKy donDangKy = duyetDonService.getDonDangKyById(maDon);

            if (maPhong != null) {
                duyetDonService.approveApplicationWithManualRoom(donDangKy, admin.getMaNV(), maPhong);
            } else {
                duyetDonService.approveApplicationAutomatically(donDangKy, admin.getMaNV());
            }
            redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt đơn #" + maDon + " thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
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
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối đơn #" + maDon);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/don-dang-ky";
    }

    @GetMapping("/hop-dong")
    public String viewHopDong(Model model) {
        model.addAttribute("pageTitle", "Quản lý Hợp Đồng");
        model.addAttribute("hopDongs", hopDongRepository.findAll());
        return "admin/hop-dong";
    }

    @GetMapping("/hoa-don")
    public String viewHoaDon(Model model) {
        model.addAttribute("pageTitle", "Quản lý Hóa Đơn");
        model.addAttribute("hoaDons", hoaDonRepository.findAll());
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
        List<TaiKhoan> taiKhoans = taiKhoanRepository.findByLoaiTK("SV");
        model.addAttribute("taiKhoans", taiKhoans);
        return "admin/tai-khoan";
    }

    @PostMapping("/tai-khoan/{maTK}/khoa")
    public String khoaTaiKhoan(@PathVariable Integer maTK, RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với mã: " + maTK));

            taiKhoan.setTrangThai(0);
            taiKhoanRepository.save(taiKhoan);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã khóa tài khoản " + taiKhoan.getTenDangNhap() + " thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi khóa tài khoản: " + e.getMessage());
        }
        return "redirect:/admin/tai-khoan";
    }

    @PostMapping("/tai-khoan/{maTK}/mo-khoa")
    public String moKhoaTaiKhoan(@PathVariable Integer maTK, RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với mã: " + maTK));

            taiKhoan.setTrangThai(1);
            taiKhoanRepository.save(taiKhoan);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã mở khóa tài khoản " + taiKhoan.getTenDangNhap() + " thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi mở khóa tài khoản: " + e.getMessage());
        }
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
        try {
            thoiGianDangKyService.createPeriod(
                    java.time.LocalDateTime.parse(ngayMo),
                    java.time.LocalDateTime.parse(ngayDong),
                    moTa);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo đợt đăng ký thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky/{maDot}/update")
    public String updateThoiGianDangKy(@PathVariable Integer maDot, @RequestParam String ngayMo,
            @RequestParam String ngayDong, @RequestParam String moTa,
            RedirectAttributes redirectAttributes) {
        try {
            thoiGianDangKyService.updatePeriod(
                    maDot,
                    java.time.LocalDateTime.parse(ngayMo),
                    java.time.LocalDateTime.parse(ngayDong),
                    moTa);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật đợt đăng ký thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thoi-gian-dang-ky";
    }

    @PostMapping("/thoi-gian-dang-ky/{maDot}/delete")
    public String deleteThoiGianDangKy(@PathVariable Integer maDot, RedirectAttributes redirectAttributes) {
        try {
            thoiGianDangKyService.deletePeriod(maDot);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đợt đăng ký thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/thoi-gian-dang-ky";
    }
}
