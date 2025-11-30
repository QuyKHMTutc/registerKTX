package com.dactaphanmem.service;

import com.dactaphanmem.model.HopDong;
import com.dactaphanmem.repository.HopDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HopDongService {

    private final HopDongRepository hopDongRepository;

    public List<HopDong> getAllHopDongs() {
        return hopDongRepository.findAll();
    }
}
