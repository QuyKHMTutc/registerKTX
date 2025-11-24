package com.dactaphanmem.service;

import com.dactaphanmem.dto.PhongForm;
import com.dactaphanmem.constant.AppConstants;
import com.dactaphanmem.model.LoaiPhong;
import com.dactaphanmem.model.Phong;
import com.dactaphanmem.model.ToaNha;
import com.dactaphanmem.repository.LoaiPhongRepository;
import com.dactaphanmem.repository.PhongRepository;
import com.dactaphanmem.repository.ToaNhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
     * Lấy danh sách tất cả các phòng, đã được sắp xếp theo Tòa nhà rồi đến Số phòng.
     * 
     * @return List<Phong> danh sách phòng đã sắp xếp.
     */
    public List<Phong> getAllPhongs() {
        List<Phong> phongs = phongRepository.findAll();
        return phongs.stream()
                .sorted(Comparator.comparing((Phong p) -> p.getToaNha().getTenToa())
                        .thenComparing(Phong::getSoPhong))
                .collect(Collectors.toList());
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

        if (phongForm.getMaToa() == null) {
            throw new IllegalArgumentException("Vui lòng chọn Tòa Nhà.");
        }
        if (phongForm.getMaLoai() == null) {
            throw new IllegalArgumentException("Vui lòng chọn Loại Phòng.");
        }

        ToaNha toaNha = toaNhaRepository.findById(phongForm.getMaToa())
                .orElseThrow(() -> new IllegalArgumentException("Mã Tòa Nhà không hợp lệ."));

        LoaiPhong loaiPhong = loaiPhongRepository.findById(phongForm.getMaLoai())
                .orElseThrow(() -> new IllegalArgumentException("Mã Loại Phòng không hợp lệ."));

        if (phongRepository.findBySoPhongAndToaNha_MaToa(phongForm.getSoPhong(), toaNha.getMaToa()).isPresent()) {
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

        // Phòng mới tạo luôn là "Sẵn sàng"
        phongForm.setTrangThai(AppConstants.PHONG_SAN_SANG);

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
    @Transactional
    public Phong updateTrangThai(Integer maPhong, String trangThaiMoi) {
        if (maPhong == null) {
            throw new IllegalArgumentException("Mã phòng không được để trống.");
        }
        if (trangThaiMoi == null || trangThaiMoi.isBlank()) {
            throw new IllegalArgumentException("Trạng thái không được để trống.");
        }

        Phong phong = phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Phòng có ID: " + maPhong));

        // Chỉ cho phép cập nhật thủ công giữa "Sẵn sàng" và "Bảo trì"
        if (!(trangThaiMoi.equals(AppConstants.PHONG_SAN_SANG) || trangThaiMoi.equals(AppConstants.PHONG_BAO_TRI))) {
            throw new IllegalArgumentException("Chỉ có thể cập nhật trạng thái thành 'Sẵn sàng' hoặc 'Bảo trì'.");
        }
        
        // Không cho phép chuyển phòng "Đầy chỗ" sang "Bảo trì" trực tiếp
        if (phong.getTrangThai().equals(AppConstants.PHONG_DAY_CHO) && trangThaiMoi.equals(AppConstants.PHONG_BAO_TRI)) {
            throw new IllegalArgumentException("Không thể bảo trì phòng đang có người ở. Vui lòng chuyển sinh viên trước.");
        }

        phong.setTrangThai(trangThaiMoi);
        return phongRepository.saveAndFlush(phong);
    }
}
