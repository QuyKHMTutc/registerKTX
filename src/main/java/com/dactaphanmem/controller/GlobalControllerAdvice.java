package com.dactaphanmem.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Lớp này cung cấp các thuộc tính chung cho tất cả các model trong ứng dụng.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final com.dactaphanmem.service.ThongBaoService thongBaoService;

    public GlobalControllerAdvice(com.dactaphanmem.service.ThongBaoService thongBaoService) {
        this.thongBaoService = thongBaoService;
    }

    /**
     * Thêm URI của request hiện tại vào model.
     * Điều này cho phép các template (như fragments.html) có thể truy cập vào đường
     * dẫn hiện tại
     * để làm nổi bật các mục menu đang hoạt động.
     *
     * @param request Request HTTP hiện tại.
     * @return Chuỗi URI của request.
     */
    @ModelAttribute("currentURI")
    public String getCurrentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Add unread notification count to all pages
     * This ensures the badge is always available in the layout
     */
    @ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount() {
        try {
            return thongBaoService.getUnreadCount();
        } catch (Exception e) {
            return 0;
        }
    }

    @ModelAttribute("recentNotifications")
    public java.util.List<com.dactaphanmem.model.ThongBao> getRecentNotifications() {
        try {
            // Get recent notifications
            return thongBaoService.getRecentNotifications();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
