package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Lớp này cung cấp các thuộc tính chung cho tất cả các model trong ứng dụng.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Thêm URI của request hiện tại vào model.
     * Điều này cho phép các template (như fragments.html) có thể truy cập vào đường dẫn hiện tại
     * để làm nổi bật các mục menu đang hoạt động.
     *
     * @param request Request HTTP hiện tại.
     * @return Chuỗi URI của request.
     */
    @ModelAttribute("currentURI")
    public String getCurrentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
