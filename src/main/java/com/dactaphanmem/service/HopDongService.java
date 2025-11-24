package com.dactaphanmem.service;

import com.dactaphanmem.model.HopDong;
import com.dactaphanmem.repository.HopDongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HopDongService {

    private final HopDongRepository hopDongRepository;

    public HopDongService(HopDongRepository hopDongRepository) {
        this.hopDongRepository = hopDongRepository;
    }

    public List<HopDong> getAllHopDongs() {
        return hopDongRepository.findAll();
    }
}
