package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Lớp này cung cấp các thuộc tính chung cho tất cả các model trong ứng dụng.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final org.example.service.ThongBaoService thongBaoService;

    public GlobalControllerAdvice(org.example.service.ThongBaoService thongBaoService) {
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
        // In a real app, we should check if the user is an admin first
        // For now, we'll just return the count from the service
        // The service logic might need to be adjusted to only count for the current
        // user
        // but for this task we are just moving the existing logic.
        try {
            return thongBaoService.getUnreadCount();
        } catch (Exception e) {
            return 0;
        }
    }
}
