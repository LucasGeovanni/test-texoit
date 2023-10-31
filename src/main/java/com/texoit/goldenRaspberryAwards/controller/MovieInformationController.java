package com.texoit.goldenRaspberryAwards.controller;

import com.texoit.goldenRaspberryAwards.controller.api.MovieInformationAPI;
import com.texoit.goldenRaspberryAwards.model.dto.GoldenRaspberryAwardsResponseDTO;
import com.texoit.goldenRaspberryAwards.service.GoldenRaspberryAwardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("/movie-information")
@RequiredArgsConstructor
public class MovieInformationController implements MovieInformationAPI {


    private final GoldenRaspberryAwardsService goldenRaspberryAwardsService;



    @GetMapping("/raspberryAwards")
    public ResponseEntity<GoldenRaspberryAwardsResponseDTO> findGoldenRaspberryAwards(@RequestParam(value = "minYear", required = false) Integer minYear, @RequestParam(value = "maxYear", required = false) Integer maxYear) {
        Optional<GoldenRaspberryAwardsResponseDTO> awardsGeneralInfo = goldenRaspberryAwardsService.findAwardsGeneralInfo(minYear, maxYear);
        return awardsGeneralInfo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

}
