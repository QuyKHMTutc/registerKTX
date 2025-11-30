package com.dactaphanmem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SinhVienForm {
    String maSV;
    String hoTen;
    String lop;
    String khoa;
    String sdt;
    String gioiTinh;
    LocalDate ngaySinh;
    String diaChi;
}
