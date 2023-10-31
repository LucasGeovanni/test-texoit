package com.texoit.goldenRaspberryAwards.controller;

import com.texoit.goldenRaspberryAwards.model.dto.AwardsIntervalResponseDTO;
import com.texoit.goldenRaspberryAwards.model.dto.GoldenRaspberryAwardsResponseDTO;
import com.texoit.goldenRaspberryAwards.service.GoldenRaspberryAwardsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@RunWith(MockitoJUnitRunner.class)
public class MovieInformationControllerTest {

    @Autowired
    MovieInformationController movieInformationController;

    @MockBean
    GoldenRaspberryAwardsService goldenRaspberryAwardsService;

    @Test
    public void get_RaspberryAwards_200() throws Exception {

        when(goldenRaspberryAwardsService.findAwardsGeneralInfo(null, null)).thenReturn(
                Optional.of(GoldenRaspberryAwardsResponseDTO.builder()
                                .max(Arrays.asList(AwardsIntervalResponseDTO.builder()
                                                .interval(1)
                                                .producer("producer 1")
                                                .followingWin(2022)
                                                .previousWin(2012)
                                                .interval(10)
                                        .build()))
                                .min(Arrays.asList(AwardsIntervalResponseDTO.builder()
                                        .interval(1)
                                        .producer("producer 1")
                                        .followingWin(2022)
                                        .previousWin(2021)
                                        .interval(1)
                                        .build()))
                        .build()));
        ResponseEntity<GoldenRaspberryAwardsResponseDTO> response = movieInformationController.findGoldenRaspberryAwards(null, null);
        assertEquals(200, response.getStatusCode().value());
    }

}
