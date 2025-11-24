package org.example.service;

import org.example.model.TaiKhoan;
import org.example.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;

    public TaiKhoanService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public List<TaiKhoan> getTaiKhoanByLoai(String loaiTK) {
        return taiKhoanRepository.findByLoaiTK(loaiTK);
    }

    public void khoaTaiKhoan(Integer maTK) {
        if (maTK == null) {
            throw new IllegalArgumentException("Mã tài khoản không được để trống");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với mã: " + maTK));
        taiKhoan.setTrangThai(0);
        taiKhoanRepository.save(taiKhoan);
    }

    public void moKhoaTaiKhoan(Integer maTK) {
        if (maTK == null) {
            throw new IllegalArgumentException("Mã tài khoản không được để trống");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với mã: " + maTK));
        taiKhoan.setTrangThai(1);
        taiKhoanRepository.save(taiKhoan);
    }
}
