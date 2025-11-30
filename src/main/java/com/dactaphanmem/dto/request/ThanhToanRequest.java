package com.dactaphanmem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThanhToanRequest {
    String phuongThuc; // Ví dụ: "chuyen_khoan", "tien_mat", "vnpay"
}
