package com.texoit.goldenRaspberryAwards.service.impl;

import com.texoit.goldenRaspberryAwards.model.dto.MovieInformationDTO;
import com.texoit.goldenRaspberryAwards.service.MovieInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReadCsvFileService {

    private final MovieInformationService movieInformationService;

    public static final String YES = "yes";

    @Value("${app.csv-file}")
    public String csvFile;

    @PostConstruct
    public void loadFile() {
        String line;
        var csvSplitBy = ";";
        var movieList = new ArrayList<MovieInformationDTO>();

        int count = 0;

        log.info("Iniciando leitura do arquivo {}", csvFile);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] split = line.split(csvSplitBy);
                if (count > 0) {
                    try {
                        var year = getInfo(0, split);
                        var title = getInfo(1, split);
                        var studios = getInfo(2, split);
                        var producers = getInfo(3, split);
                        var winner = getInfo(4, split);
                        movieList.add(MovieInformationDTO.builder()
                                .year(Objects.nonNull(year) ? Integer.valueOf(year) : null)
                                .title(title)
                                .studios(studios)
                                .producers(producers)
                                .winner(isWinner(winner))
                                .build());
                    } catch (Exception e) {
                        log.error("Erro ao ler a linha {}", count);
                    }
                }
                count++;

            }
        } catch (IOException e) {
            log.error("Erro ao processar o arquivo {}", csvFile, e);
        } finally {
            log.info("Leitura concluida, quantidade de registros {}", movieList.size());
            save(movieList);
        }
    }

    private String getInfo(int position, String[] split) {
        try {
            return split[position];
        } catch (IndexOutOfBoundsException outOfBoundsException) {
            return null;
        }
    }

    public void save(List<MovieInformationDTO> movieInformationList) {
        movieInformationList.forEach(dto-> {
            try {
                movieInformationService.save(dto);
            } catch (Exception e) {
                log.error("Erro ao salvar o registro {}", movieInformationService, e);
            }

        });

    }

    private boolean isWinner(String value) {
        return YES.equals(value);
    }
}
