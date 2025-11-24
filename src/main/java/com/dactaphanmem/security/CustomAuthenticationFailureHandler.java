package com.dactaphanmem.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("tenDangNhap");
        String encodedUsername = (username != null) ? URLEncoder.encode(username, StandardCharsets.UTF_8) : "";
        String failureUrl;

        if (exception instanceof DisabledException) {
            // Lỗi tài khoản chưa kích hoạt (isEnabled = false)
            failureUrl = "/auth/login?not_activated=true&username=" + encodedUsername;
        } else if (exception instanceof LockedException) {
            // Lỗi tài khoản bị khóa (trangThai != 1)
            failureUrl = "/auth/login?locked=true&username=" + encodedUsername;
        } else {
            // Các lỗi khác (sai mật khẩu, user không tồn tại)
            failureUrl = "/auth/login?error=true&username=" + encodedUsername;
        }
        
        getRedirectStrategy().sendRedirect(request, response, failureUrl);
    }
}
