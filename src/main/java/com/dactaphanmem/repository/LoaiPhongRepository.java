package com.dactaphanmem.repository;

import com.dactaphanmem.model.LoaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, Integer> {
    // Repository cho LoaiPhong
}