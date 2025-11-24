package org.example.repository;

import org.example.model.LoaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, Integer> {
    // Repository cho LoaiPhong
}