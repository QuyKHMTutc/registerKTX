package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String requestURI = request.getRequestURI();

        // Extract first validation error message
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");

        logger.warn("Validation error at {}: {}", requestURI, errorMessage);

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", errorMessage));
        }

        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:" + getReferer(request);
    }

    /**
     * Handle business logic exceptions (e.g., invalid arguments, state violations)
     */
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public Object handleBusinessException(RuntimeException ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String requestURI = request.getRequestURI();
        logger.warn("Business exception at {}: {}", requestURI, ex.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:" + getReferer(request);
    }

    /**
     * Handle database integrity violations (e.g., duplicate keys)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Throwable rootCauseException = ex.getRootCause();
        String rootCause = rootCauseException != null ? rootCauseException.getMessage() : ex.getMessage();
        String errorMessage = "Lỗi dữ liệu: ";

        if (rootCause != null) {
            if (rootCause.contains("Duplicate entry")) {
                if (rootCause.contains("'phong.so_phong'")) {
                    errorMessage = "Số phòng đã tồn tại trong tòa nhà này.";
                } else if (rootCause.contains("'sinh_vien.ma_sv'")) {
                    errorMessage = "Mã sinh viên đã tồn tại.";
                } else if (rootCause.contains("'tai_khoan.ten_dang_nhap'")) {
                    errorMessage = "Tên đăng nhập đã tồn tại.";
                } else if (rootCause.contains("'tai_khoan.email'")) {
                    errorMessage = "Email đã tồn tại.";
                } else {
                    errorMessage = "Dữ liệu đã tồn tại.";
                }
            } else {
                errorMessage = "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc.";
            }
        }

        logger.warn("Data integrity violation at {}: {}", request.getRequestURI(), errorMessage);

        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", errorMessage));
        }

        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:" + getReferer(request);
    }

    /**
     * Handle unexpected system errors
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String requestURI = request.getRequestURI();
        logger.error("Unhandled exception at {}: {}", requestURI, ex.getMessage(), ex);

        String genericMessage = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.";

        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", genericMessage));
        }

        redirectAttributes.addFlashAttribute("errorMessage", genericMessage);
        return "redirect:" + getReferer(request);
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI != null && requestURI.startsWith("/api/");
    }

    private String getReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isEmpty()) ? referer : "/";
    }
}
