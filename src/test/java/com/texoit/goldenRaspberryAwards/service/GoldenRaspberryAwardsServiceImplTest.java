package com.texoit.goldenRaspberryAwards.service;


import com.texoit.goldenRaspberryAwards.model.dto.AwardsIntervalResponseDTO;
import com.texoit.goldenRaspberryAwards.model.dto.GoldenRaspberryAwardsResponseDTO;
import com.texoit.goldenRaspberryAwards.model.dto.MovieInformationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoldenRaspberryAwardsServiceImplTest {

    @Autowired
    GoldenRaspberryAwardsServiceImpl goldenRaspberryAwardsService;

    @MockBean
    MovieInformationService movieInformationService;

    @Test
    public void test1() {
        when(movieInformationService.findMovieInformationsByIntervals(isNull(), isNull())).thenReturn(Arrays.asList(
                MovieInformationDTO.builder()
                        .winner(true)
                        .year(2002)
                        .title("title")
                        .producers("producer 1")
                        .studios("studio")
                .build(),
                MovieInformationDTO.builder()
                        .winner(true)
                        .year(2001)
                        .title("title")
                        .producers("producer 1")
                        .studios("studio")
                        .build(),
                MovieInformationDTO.builder()
                        .winner(true)
                        .year(2020)
                        .title("title")
                        .producers("producer 1")
                        .studios("studio")
                        .build()));
        Optional<GoldenRaspberryAwardsResponseDTO> awardsGeneralInfo = goldenRaspberryAwardsService.findAwardsGeneralInfo(null, null);
        awardsGeneralInfo.ifPresent(awards-> {
            assertEquals(18, awards.getMax().stream().findFirst().map(AwardsIntervalResponseDTO::getInterval).orElseThrow());
            assertEquals(1, awards.getMin().stream().findFirst().map(AwardsIntervalResponseDTO::getInterval).orElseThrow());
        });
    }
}
