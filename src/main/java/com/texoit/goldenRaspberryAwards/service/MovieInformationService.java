package com.texoit.goldenRaspberryAwards.service;

import com.texoit.goldenRaspberryAwards.model.dto.MovieInformationDTO;

import java.util.List;

public interface MovieInformationService {

    void save(MovieInformationDTO movieInformationDTO);
    List<MovieInformationDTO> findMovieInformationsByIntervals(Integer minYear, Integer maxYear);

}
