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

        Map<String, List<MovieInformationDTO>> consecutiveGroup = splitByProducers.stream()
                .collect(Collectors.groupingBy(MovieInformationDTO::getProducers)).entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<AwardsIntervalResponseDTO> list = mountIntervals(consecutiveGroup);

        var minInterval = list.stream()
                .map(AwardsIntervalResponseDTO::getInterval)
                .min(Integer::compare).orElse(0);

        var maxInterval = list.stream()
                .map(AwardsIntervalResponseDTO::getInterval)
                .max(Integer::compare).orElse(Integer.MAX_VALUE);

        List<AwardsIntervalResponseDTO> min = list.stream().filter(x-> x.getInterval().equals(minInterval)).collect(Collectors.toList());
        List<AwardsIntervalResponseDTO> max = list.stream().filter(x-> x.getInterval().equals(maxInterval)).collect(Collectors.toList());

        return Optional.of(GoldenRaspberryAwardsResponseDTO.builder()
                .min(min)
                .max(max)
                .build());
    }

    private List<AwardsIntervalResponseDTO> mountIntervals(Map<String, List<MovieInformationDTO>> producersGroup) {
        List<AwardsIntervalResponseDTO> response = new ArrayList<>();
        producersGroup.forEach((key, producersList)-> {
            sortByYear(producersList);
            for (int i = 0; i < producersList.size()-1; i++) {
                MovieInformationDTO first = producersList.get(i);
                MovieInformationDTO second = producersList.get(i+1);
                response.add(getIntervals2(key, first.getYear(), second.getYear()));
            }
        });
        return response;
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


    private Map<String, List<MovieInformationDTO>> getIntervals(MovieInformationDTO first, MovieInformationDTO second, Integer sizeIndex) {
        Map<String, List<MovieInformationDTO>> map = new HashMap<>();
        int interval = second.getYear() - first.getYear();
        first.setInterval(interval);
        second.setInterval(interval);
        map.put(String.format("%s%s", sizeIndex.toString(), interval), Arrays.asList(first, second));
        return map;
    }
    private AwardsIntervalResponseDTO getIntervals2(String producer, Integer firstYear, Integer secondYear) {
        int interval = secondYear - firstYear;
        return AwardsIntervalResponseDTO.builder()
                .interval(interval)
                .previousWin(firstYear)
                .followingWin(secondYear)
                .producer(producer)
                .build();
    }

    private void sortByYear(List<MovieInformationDTO> list) {
        list.sort(Comparator.comparing(MovieInformationDTO::getYear));
    }

}
