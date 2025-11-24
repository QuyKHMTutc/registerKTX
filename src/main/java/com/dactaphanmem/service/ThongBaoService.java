package com.dactaphanmem.service;

import com.dactaphanmem.model.NhanVien;
import com.dactaphanmem.model.ThongBao;
import com.dactaphanmem.repository.NhanVienRepository;
import com.dactaphanmem.repository.ThongBaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing admin notifications.
 * Handles creation, retrieval, and status updates of system notifications.
 */
@Service
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final NhanVienRepository nhanVienRepository;

    public ThongBaoService(ThongBaoRepository thongBaoRepository,
            NhanVienRepository nhanVienRepository) {
        this.thongBaoRepository = thongBaoRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    /**
     * Create notifications for all administrators in the system.
     * This method broadcasts a notification to every admin user.
     * 
     * @param tieuDe  Notification title
     * @param noiDung Notification content/body
     * @param loai    Notification type (e.g., "don_dang_ky", "thanh_toan")
     * @param maDon   Related application ID (optional, can be null)
     */
    @Transactional
    public void createNotification(String tieuDe, String noiDung, String loai, Integer maDon) {
        // Get all admins
        List<NhanVien> admins = nhanVienRepository.findAll();

        // Create a notification for each admin
        for (NhanVien admin : admins) {
            ThongBao thongBao = new ThongBao();
            thongBao.setTieuDe(tieuDe);
            thongBao.setNoiDung(noiDung);
            thongBao.setLoai(loai);
            thongBao.setMaDon(maDon);
            thongBao.setDaDoc(false);
            thongBao.setNgayTao(LocalDateTime.now());
            thongBao.setMaNguoiNhan(admin.getMaNV());

            thongBaoRepository.save(thongBao);
        }
    }

    /**
     * Retrieve all unread notifications, ordered by creation date (newest first).
     * 
     * @return List of unread notifications
     */
    public List<ThongBao> getUnreadNotifications() {
        return thongBaoRepository.findByDaDocFalseOrderByNgayTaoDesc();
    }

    /**
     * Get the total count of unread notifications.
     * Useful for displaying notification badges.
     * 
     * @return Number of unread notifications
     */
    public long getUnreadCount() {
        return thongBaoRepository.countByDaDocFalse();
    }

    /**
     * Get the 10 most recent notifications regardless of read status.
     * 
     * @return List of up to 10 recent notifications
     */
    public List<ThongBao> getRecentNotifications() {
        return thongBaoRepository.findTop10ByOrderByNgayTaoDesc();
    }

    /**
     * Mark a specific notification as read.
     * 
     * @param maThongBao Notification ID to mark as read
     * @throws IllegalArgumentException if notification not found
     */
    @Transactional
    public void markAsRead(Integer maThongBao) {
        ThongBao thongBao = thongBaoRepository.findById(maThongBao)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông báo với mã: " + maThongBao));
        thongBao.setDaDoc(true);
        thongBaoRepository.save(thongBao);
    }

    /**
     * Mark all unread notifications as read.
     * Typically used for "Mark all as read" functionality.
     */
    @Transactional
    public void markAllAsRead() {
        List<ThongBao> unreadNotifications = thongBaoRepository.findByDaDocFalseOrderByNgayTaoDesc();
        unreadNotifications.forEach(notification -> notification.setDaDoc(true));
        thongBaoRepository.saveAll(unreadNotifications);
    }
}
