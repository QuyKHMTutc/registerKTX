package com.dactaphanmem.repository;

import com.dactaphanmem.model.DonDangKy;
import com.dactaphanmem.model.YeuCauChonPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface YeuCauChonPhongRepository extends JpaRepository<YeuCauChonPhong, Integer> {
    
    Optional<YeuCauChonPhong> findByDonDangKy(DonDangKy donDangKy);
}
