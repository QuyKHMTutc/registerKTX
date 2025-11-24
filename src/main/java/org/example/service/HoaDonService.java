package org.example.service;

import org.example.model.HoaDon;
import org.example.repository.HoaDonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;

    public HoaDonService(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    public List<HoaDon> getAllHoaDons() {
        return hoaDonRepository.findAll();
    }
}
