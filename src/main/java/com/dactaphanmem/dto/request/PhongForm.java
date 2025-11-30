package com.dactaphanmem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO for creating/updating room information.
 * Contains validation rules for room data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhongForm {

    @NotBlank(message = "Số phòng không được để trống")
    String soPhong;

    @NotNull(message = "Tầng không được để trống")
    @Min(value = 1, message = "Tầng phải lớn hơn 0")
    Integer tang;

    @NotBlank(message = "Giới tính không được để trống")
    String gioiTinh;

    // @NotBlank(message = "Trạng thái không được để trống") - Removed as it is set
    // in service
    String trangThai;

    @NotNull(message = "Mã loại phòng không được để trống")
    Integer maLoai;

    @NotNull(message = "Mã tòa nhà không được để trống")
    Integer maToa;
}
