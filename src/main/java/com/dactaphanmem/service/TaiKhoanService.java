package com.dactaphanmem.service;

import com.dactaphanmem.model.TaiKhoan;
import com.dactaphanmem.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;

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
