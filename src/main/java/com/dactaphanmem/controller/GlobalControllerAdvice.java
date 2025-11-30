package com.dactaphanmem.controller;

import com.dactaphanmem.service.ThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ThongBaoService thongBaoService;

    @ModelAttribute("currentURI")
    public String getCurrentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

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
            return thongBaoService.getRecentNotifications();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
