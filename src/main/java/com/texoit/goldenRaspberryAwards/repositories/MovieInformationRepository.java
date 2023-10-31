package com.texoit.goldenRaspberryAwards.repositories;

import com.texoit.goldenRaspberryAwards.model.entities.MovieInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieInformationRepository extends JpaRepository<MovieInformation, Long> {


    @Query("SELECT m FROM MovieInformation m WHERE m.year >= :min AND m.year <= :max")
    List<MovieInformation> findByIntervals(Integer min, Integer max);


}
