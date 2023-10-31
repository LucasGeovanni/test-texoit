package com.texoit.goldenRaspberryAwards.service;

import com.texoit.goldenRaspberryAwards.model.dto.AwardsIntervalResponseDTO;
import com.texoit.goldenRaspberryAwards.model.dto.GoldenRaspberryAwardsResponseDTO;
import com.texoit.goldenRaspberryAwards.model.dto.MovieInformationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoldenRaspberryAwardsServiceImpl implements GoldenRaspberryAwardsService {

    private final MovieInformationService movieInformationService;

    @Override
    public Optional<GoldenRaspberryAwardsResponseDTO> findAwardsGeneralInfo(Integer minYear, Integer maxYear) {
        List<MovieInformationDTO> movieInformationsByIntervals = movieInformationService.findMovieInformationsByIntervals(minYear, maxYear)
                .stream()
                .filter(MovieInformationDTO::getWinner)
                .collect(Collectors.toList());

       if(ObjectUtils.isEmpty(movieInformationsByIntervals)) {
           log.info("Nenhum registro encontrado!");
            return Optional.empty();
        }
       // TODO agrupar logicas
        return Optional.of(GoldenRaspberryAwardsResponseDTO.builder()
                .min(calculateSmallestRange(movieInformationsByIntervals))
                .max(calculateLargestRange(movieInformationsByIntervals))
                .build());
    }

    private List<AwardsIntervalResponseDTO> calculateLargestRange(List<MovieInformationDTO> movies) {
        List<AwardsIntervalResponseDTO> result = new ArrayList<>();
        Map<String, List<MovieInformationDTO>> group = movies.stream().collect(Collectors.groupingBy(MovieInformationDTO::getProducers));
        group.forEach((producerName, producersList)-> {
            if (producersList.size() > 1) {  Map<Integer, List<MovieInformationDTO>> intervals = mountIntervals(producersList);

                int maxInterval = intervals.keySet().stream()
                        .mapToInt(Integer::intValue)
                        .max()
                        .getAsInt();

                List<MovieInformationDTO> movieInformationDTOS = getMovieInformationByKey(intervals, maxInterval);
                List<Integer> yearsList = movieInformationDTOS.stream().map(MovieInformationDTO::getYear).collect(Collectors.toList());
                int previous = Collections.min(yearsList);
                int following = Integer.MAX_VALUE;

                for (Integer valor : yearsList) {
                    if (valor > previous && valor < following) {
                        following = valor;
                    }
                }
                result.add(AwardsIntervalResponseDTO.builder()
                        .previousWin(previous)
                        .followingWin(following)
                        .producer(producerName)
                        .interval(maxInterval)
                        .build());
            }
        });
        if (!result.isEmpty()) {
            int indexResult = result.stream().map(AwardsIntervalResponseDTO::getInterval).mapToInt(x -> x).max()
                    .orElseThrow(NoSuchElementException::new);
            return result.stream().filter(r -> r.getInterval() == indexResult).collect(Collectors.toList());
        }
        log.info("\"max\" Vazio!");
        return Collections.emptyList();
    }

    private List<MovieInformationDTO> getMovieInformationByKey(Map<Integer, List<MovieInformationDTO>> intervals, int maxInterval) {
        return intervals.get(maxInterval);
    }

    private List<AwardsIntervalResponseDTO> calculateSmallestRange(List<MovieInformationDTO> movies) {
        List<AwardsIntervalResponseDTO> result = new ArrayList<>();
        Map<String, List<MovieInformationDTO>> producers = movies.stream().collect(Collectors.groupingBy(MovieInformationDTO::getProducers));
        producers.forEach((producerName, producersList)-> {
            if (producersList.size() > 1) {
                Map<Integer, List<MovieInformationDTO>> intervals = mountIntervals(producersList);

                int minInterval = intervals.keySet().stream()
                        .mapToInt(Integer::intValue)
                        .min()
                        .getAsInt();

                List<MovieInformationDTO> movieInformationDTOS = getMovieInformationByKey(intervals, minInterval);
                List<Integer> yearsList = movieInformationDTOS.stream().map(MovieInformationDTO::getYear).collect(Collectors.toList());
                int previous = Collections.min(yearsList);
                int following = Integer.MAX_VALUE;

                for (Integer valor : yearsList) {
                    if (valor > previous && valor < following) {
                        following = valor;
                    }
                }
                result.add(AwardsIntervalResponseDTO.builder()
                        .previousWin(previous)
                        .followingWin(following)
                        .producer(producerName)
                        .interval(minInterval)
                        .build());
            }
        });
        if (!result.isEmpty()) {
            int indexResult = result.stream().map(AwardsIntervalResponseDTO::getInterval).mapToInt(x -> x).min()
                    .orElseThrow(NoSuchElementException::new);
            return result.stream().filter(r-> r.getInterval() == indexResult).collect(Collectors.toList());
        }
        log.info("\"min\" Vazio!");
        return Collections.emptyList();
    }

    private Map<Integer, List<MovieInformationDTO>> mountIntervals(List<MovieInformationDTO> producersList) {
        sortByYear(producersList);
        Map<Integer, List<MovieInformationDTO>> intervals = new HashMap<>();
        for (int i = 0; i < producersList.size()-1; i++) {
            MovieInformationDTO first = producersList.get(i);
            MovieInformationDTO second = producersList.get(i+1);
            intervals.putAll(getIntervals(first, second));
        }
        return intervals;
    }

    private Map<Integer, List<MovieInformationDTO>> getIntervals(MovieInformationDTO first, MovieInformationDTO second) {
        Map<Integer, List<MovieInformationDTO>> map = new HashMap<>();
        map.put(second.getYear() - first.getYear(), Arrays.asList(first, second));
        return map;
    }

    private void sortByYear(List<MovieInformationDTO> list) {
        list.sort(Comparator.comparing(MovieInformationDTO::getYear));
    }



}
