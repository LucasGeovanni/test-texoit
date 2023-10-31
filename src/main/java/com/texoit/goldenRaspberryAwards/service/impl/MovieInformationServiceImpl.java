package com.texoit.goldenRaspberryAwards.service.impl;

import com.texoit.goldenRaspberryAwards.model.dto.MovieInformationDTO;
import com.texoit.goldenRaspberryAwards.model.entities.MovieInformation;
import com.texoit.goldenRaspberryAwards.repositories.MovieInformationRepository;
import com.texoit.goldenRaspberryAwards.service.MovieInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieInformationServiceImpl implements MovieInformationService {

    private final MovieInformationRepository movieInformationRepository;

    @Override
    public void save(MovieInformationDTO movieInformationDTO) {
        log.info("Salvando informação do file {}", movieInformationDTO);
        movieInformationRepository.save(MovieInformation.builder()
                        .year(movieInformationDTO.getYear())
                        .title(movieInformationDTO.getTitle())
                        .studios(movieInformationDTO.getStudios())
                        .producers(movieInformationDTO.getProducers())
                        .winner(movieInformationDTO.getWinner())
                .build());
    }

    @Override
    public List<MovieInformationDTO> findMovieInformationsByIntervals(Integer minYear, Integer maxYear) {
        List<MovieInformation> movieInformations;
        if (Objects.nonNull(minYear) || Objects.nonNull(maxYear)) {
            movieInformations = movieInformationRepository.findByIntervals(minYear, maxYear);
        } else {
            movieInformations = movieInformationRepository.findAll();
        }
        return movieInformations
                .stream().map(x-> MovieInformationDTO.builder()
                        .producers(x.getProducers())
                        .studios(x.getStudios())
                        .title(x.getTitle())
                        .year(x.getYear())
                        .winner(x.getWinner())
                        .build()).collect(Collectors.toList());
    }


}
