package com.dactaphanmem.service;

import com.dactaphanmem.model.HoaDon;
import com.dactaphanmem.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;

    public List<HoaDon> getAllHoaDons() {
        return hoaDonRepository.findAll();
    }
}
