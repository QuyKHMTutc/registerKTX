package com.dactaphanmem.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SinhVienForm {
    private String maSV;
    private String hoTen;
    private String lop;
    private String khoa;
    private String sdt;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private String diaChi;
}
