package org.example.service;

import org.example.dto.PhongForm;
import org.example.constant.AppConstants;
import org.example.model.LoaiPhong;
import org.example.model.Phong;
import org.example.model.ToaNha;
import org.example.repository.LoaiPhongRepository;
import org.example.repository.PhongRepository;
import org.example.repository.ToaNhaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PhongService {

    private final PhongRepository phongRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final LoaiPhongRepository loaiPhongRepository;

    public PhongService(PhongRepository phongRepository,
            ToaNhaRepository toaNhaRepository,
            LoaiPhongRepository loaiPhongRepository) {
        this.phongRepository = phongRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.loaiPhongRepository = loaiPhongRepository;
    }

    /**
     * Lấy danh sách tất cả các phòng.
     * 
     * @return List<Phong> danh sách phòng.
     */
    public List<Phong> getAllPhongs() {
        return phongRepository.findAll();
    }

    /**
     * Tạo mới một phòng.
     * 
     * @param phongForm DTO chứa thông tin phòng cần tạo.
     * @return Phong Phòng mới được tạo.
     */
    public Phong createPhong(PhongForm phongForm) {
        if (phongForm == null) {
            throw new IllegalArgumentException("Thiếu thông tin phòng cần tạo.");
        }

        ToaNha toaNha = toaNhaRepository.findById(phongForm.getMaToa())
                .orElseThrow(() -> new IllegalArgumentException("Mã Tòa Nhà không hợp lệ."));

        LoaiPhong loaiPhong = loaiPhongRepository.findById(phongForm.getMaLoai())
                .orElseThrow(() -> new IllegalArgumentException("Mã Loại Phòng không hợp lệ."));

        if (phongRepository.findBySoPhongAndToaNha(phongForm.getSoPhong(), toaNha).isPresent()) {
            throw new IllegalArgumentException("Số phòng đã tồn tại trong tòa nhà này.");
        }

        String gioiTinhPhong = phongForm.getGioiTinh();
        String loaiToa = toaNha.getLoaiToa();

        // Kiểm tra null để tránh NullPointerException
        if (loaiToa == null) {
            throw new IllegalArgumentException("Tòa nhà không có thông tin về loại tòa.");
        }

        if (!AppConstants.GIOI_TINH_KHONG_YEU_CAU.equalsIgnoreCase(gioiTinhPhong)
                && !loaiToa.equalsIgnoreCase(gioiTinhPhong)) {
            throw new IllegalArgumentException(
                    "Giới tính phòng (" + gioiTinhPhong + ") không phù hợp với loại Tòa Nhà (" + loaiToa + ").");
        }

        // Logic mới: Phòng mới tạo không thể là "Đầy chỗ"
        if (AppConstants.PHONG_DAY_CHO.equals(phongForm.getTrangThai())) {
            phongForm.setTrangThai(AppConstants.PHONG_SAN_SANG);
        }

        Phong newPhong = new Phong();
        newPhong.setSoPhong(phongForm.getSoPhong());
        newPhong.setTang(phongForm.getTang());
        newPhong.setGioiTinh(gioiTinhPhong);
        newPhong.setTrangThai(phongForm.getTrangThai());
        newPhong.setToaNha(toaNha);
        newPhong.setLoaiPhong(loaiPhong);

        return phongRepository.save(newPhong);
    }

    /**
     * Cập nhật trạng thái phòng.
     * 
     * @param maPhong      Mã phòng cần cập nhật.
     * @param trangThaiMoi Trạng thái mới.
     * @return Phong Phòng sau khi cập nhật.
     */
    public Phong updateTrangThai(Integer maPhong, String trangThaiMoi) {
        if (maPhong == null) {
            throw new IllegalArgumentException("Mã phòng không được để trống.");
        }
        if (trangThaiMoi == null || trangThaiMoi.isBlank()) {
            throw new IllegalArgumentException("Trạng thái không được để trống.");
        }

        // Logic mới: Không cho phép set thủ công sang "Đầy chỗ"
        if (AppConstants.PHONG_DAY_CHO.equals(trangThaiMoi)) {
            throw new IllegalArgumentException("Trạng thái 'Đầy chỗ' được cập nhật tự động, không thể set thủ công.");
        }

        Phong phong = phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Phòng có ID: " + maPhong));
        phong.setTrangThai(trangThaiMoi);
        return phongRepository.save(phong);
    }
}
