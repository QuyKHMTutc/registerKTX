package org.example.repository;

import org.example.model.DonDangKy;
import org.example.model.YeuCauChonPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface YeuCauChonPhongRepository extends JpaRepository<YeuCauChonPhong, Integer> {
    
    Optional<YeuCauChonPhong> findByDonDangKy(DonDangKy donDangKy);
}
