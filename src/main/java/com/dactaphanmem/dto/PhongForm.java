package com.dactaphanmem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for creating/updating room information.
 * Contains validation rules for room data.
 */
@Data
public class PhongForm {

    @NotBlank(message = "Số phòng không được để trống")
    private String soPhong;

    @NotNull(message = "Tầng không được để trống")
    @Min(value = 1, message = "Tầng phải lớn hơn 0")
    private Integer tang;

    @NotBlank(message = "Giới tính không được để trống")
    private String gioiTinh;

    // @NotBlank(message = "Trạng thái không được để trống") - Removed as it is set
    // in service
    private String trangThai;

    @NotNull(message = "Mã loại phòng không được để trống")
    private Integer maLoai;

    @NotNull(message = "Mã tòa nhà không được để trống")
    private Integer maToa;
}
