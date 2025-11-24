package com.dactaphanmem.repository;

import com.dactaphanmem.model.Phong;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PhongRepository extends JpaRepository<Phong, Integer> {

    /**
     * Tìm một phòng dựa trên số phòng và mã tòa nhà.
     * Dùng để kiểm tra tính duy nhất của phòng trong một tòa nhà.
     */
    Optional<Phong> findBySoPhongAndToaNha_MaToa(String soPhong, Integer maToa);

    /**
     * Tìm các phòng theo trạng thái.
     */
    List<Phong> findByTrangThai(String trangThai);

    /**
     * Tìm các phòng còn trống theo giới tính.
     */
    List<Phong> findByTrangThaiAndGioiTinh(String trangThai, String gioiTinh);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Phong p WHERE p.maPhong = :maPhong")
    Optional<Phong> findByIdWithLock(@Param("maPhong") Integer maPhong);
}
