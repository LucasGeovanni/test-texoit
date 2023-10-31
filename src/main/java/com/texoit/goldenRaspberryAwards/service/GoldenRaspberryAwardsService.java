package com.texoit.goldenRaspberryAwards.service;

import com.texoit.goldenRaspberryAwards.model.dto.GoldenRaspberryAwardsResponseDTO;

import java.util.Optional;

public interface GoldenRaspberryAwardsService {

    Optional<GoldenRaspberryAwardsResponseDTO> findAwardsGeneralInfo(Integer minYear, Integer maxYear);
}
