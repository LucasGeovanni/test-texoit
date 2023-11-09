package com.texoit.goldenRaspberryAwards.service;

import com.texoit.goldenRaspberryAwards.model.dto.AwardsIntervalResponseDTO;
import com.texoit.goldenRaspberryAwards.model.dto.GoldenRaspberryAwardsResponseDTO;
import com.texoit.goldenRaspberryAwards.model.dto.MovieInformationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoldenRaspberryAwardsServiceImpl implements GoldenRaspberryAwardsService {

    private final MovieInformationService movieInformationService;

    private final String regex =  ", |and ";
   private final Pattern pattern = Pattern.compile(regex);

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
        List<MovieInformationDTO> splitByProducers = splitByProducer(movieInformationsByIntervals);
        Map<String, List<MovieInformationDTO>> producersGroup = splitByProducers.stream().collect(Collectors.groupingBy(MovieInformationDTO::getProducers));
        Map<String, List<MovieInformationDTO>> consecutiveGroup = producersGroup.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Map<Integer, List<MovieInformationDTO>>> intervals = mountIntervals(consecutiveGroup);

        var minInterval = intervals.stream()
                .flatMap(map -> map.keySet().stream())
                .min(Integer::compareTo).orElse(0);

        var maxInterval = intervals.stream()
                .flatMap(map -> map.keySet().stream())
                .max(Integer::compareTo).orElse(Integer.MAX_VALUE);

        List<AwardsIntervalResponseDTO> min = calculateRange(intervals, minInterval, "min");
        List<AwardsIntervalResponseDTO> max = calculateRange(intervals, maxInterval, "max");

        return Optional.of(GoldenRaspberryAwardsResponseDTO.builder()
                .min(min)
                .max(max)
                .build());
    }

    private List<MovieInformationDTO> splitByProducer(List<MovieInformationDTO> movieInformationsByIntervals) {
        List<MovieInformationDTO> splitByProducers = new ArrayList<>();
        movieInformationsByIntervals.forEach(movieInformation-> {
            if (pattern.matcher(movieInformation.getProducers()).find()) {

                setMultipleProducers(splitByProducers, movieInformation);

            } else {
                splitByProducers.add(movieInformation);
            }
        });

        return splitByProducers;
    }

    private void setMultipleProducers(List<MovieInformationDTO> splitByProducers, MovieInformationDTO movieInformation) {
        String[] producers = movieInformation.getProducers().split(regex);

        Arrays.stream(producers).forEach(producer -> {
            splitByProducers.add(MovieInformationDTO.builder()
                            .producers(producer)
                            .studios(movieInformation.getStudios())
                            .title(movieInformation.getTitle())
                            .year(movieInformation.getYear())
                            .winner(movieInformation.getWinner())
                    .build());
        });
    }

    private List<AwardsIntervalResponseDTO> calculateRange(List<Map<Integer, List<MovieInformationDTO>>> producerGroup, int interval, String type) {
        List<AwardsIntervalResponseDTO> result = new ArrayList<>();

        producerGroup.forEach(y-> y.entrySet().stream().filter(x-> x.getKey().equals(interval)).forEach(map-> {
            List<Integer> yearsListMin = map.getValue().stream().map(MovieInformationDTO::getYear).collect(Collectors.toList());
                    var previous = Collections.min(yearsListMin);
                    var following = Integer.MAX_VALUE;

                    var producer = map.getValue().stream().findFirst().map(MovieInformationDTO::getProducers).orElseThrow();

                    for (Integer valor : yearsListMin) {
                        if (valor > previous && valor < following) {
                            following = valor;
                        }
                    }
                    result.add(AwardsIntervalResponseDTO.builder()
                            .previousWin(previous)
                            .followingWin(following)
                            .producer(producer)
                            .interval(interval)
                            .build());
                })
        );
        return filterResults(result, type);
    }

    private List<AwardsIntervalResponseDTO> filterResults(List<AwardsIntervalResponseDTO> result, String type) {
        if (!result.isEmpty()) {
            Integer interval = interval(type, result.stream()
                    .map(AwardsIntervalResponseDTO::getInterval).collect(Collectors.toList()));

            return result.stream().filter(r -> interval.equals(r.getInterval())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Integer interval(String type, List<Integer> intervals) {
        if ("max".equals(type)) {
           return intervals.stream().max(Comparator.comparing(value -> value)).orElse(0);
        } else {
            return intervals.stream().min(Comparator.comparing(value -> value)).orElse(0);
        }
    }


    private List<Map<Integer, List<MovieInformationDTO>>> mountIntervals(Map<String, List<MovieInformationDTO>> producersGroup) {
        List<Map<Integer, List<MovieInformationDTO>>> response = new ArrayList<>();
        producersGroup.forEach((key, producersList)-> {
            Map<Integer, List<MovieInformationDTO>> intervals = new HashMap<>();
            sortByYear(producersList);
            for (int i = 0; i < producersList.size()-1; i++) {
                MovieInformationDTO first = producersList.get(i);
                MovieInformationDTO second = producersList.get(i+1);
                intervals.putAll(getIntervals(first, second));
            }
            response.add(intervals);
        });
        return response;
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
