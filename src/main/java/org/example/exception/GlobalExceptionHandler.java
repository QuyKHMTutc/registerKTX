package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public Object handleBusinessException(Exception ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String requestURI = request.getRequestURI();
        logger.warn("Business exception at {}: {}", requestURI, ex.getMessage());

        // Xử lý API endpoints - trả về ResponseEntity
        if (requestURI != null && requestURI.startsWith("/api/")) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        // Xử lý web endpoints - redirect
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String errorMessage = "Lỗi dữ liệu: ";
        String rootCause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (rootCause != null) {
            if (rootCause.contains("Duplicate entry")) {
                if (rootCause.contains("'phong.so_phong'")) {
                    errorMessage += "Số phòng đã tồn tại trong tòa nhà này. Vui lòng chọn số phòng khác.";
                } else if (rootCause.contains("'sinh_vien.ma_sv'")) {
                    errorMessage += "Mã sinh viên đã tồn tại. Vui lòng sử dụng mã khác.";
                } else if (rootCause.contains("'tai_khoan.ten_dang_nhap'")) {
                    errorMessage += "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.";
                } else if (rootCause.contains("'tai_khoan.email'")) {
                    errorMessage += "Email đã tồn tại. Vui lòng sử dụng email khác.";
                } else {
                    errorMessage += "Dữ liệu bạn nhập đã tồn tại hoặc vi phạm ràng buộc duy nhất.";
                }
            } else if (rootCause.contains("cannot be null")) {
                errorMessage += "Một trường bắt buộc không được bỏ trống.";
            } else {
                errorMessage += "Dữ liệu bạn nhập không hợp lệ hoặc vi phạm ràng buộc cơ sở dữ liệu.";
            }
        } else {
            errorMessage += "Dữ liệu bạn nhập không hợp lệ.";
        }

        // Xử lý API endpoints - trả về ResponseEntity
        String requestURI = request.getRequestURI();
        if (requestURI != null && requestURI.startsWith("/api/")) {
            logger.warn("Data integrity violation in API: {}", errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        // Xử lý web endpoints - redirect
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String requestURI = request.getRequestURI();
        logger.error("Unhandled exception at {}: {}", requestURI, ex.getMessage(), ex);

        String genericMessage = "Có lỗi hệ thống không mong muốn đã xảy ra. Vui lòng thử lại sau.";

        // Xử lý API endpoints - trả về ResponseEntity
        if (requestURI != null && requestURI.startsWith("/api/")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericMessage);
        }

        // Xử lý web endpoints - redirect
        redirectAttributes.addFlashAttribute("errorMessage", genericMessage);
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/";
    }
}
