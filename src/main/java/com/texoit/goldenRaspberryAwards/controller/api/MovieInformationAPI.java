package com.texoit.goldenRaspberryAwards.controller.api;

import com.texoit.goldenRaspberryAwards.model.dto.GoldenRaspberryAwardsResponseDTO;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface MovieInformationAPI {


    @ApiOperation(value = "Retorna as informações do Golden Raspberry Awards")
    @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "Sucesso ao buscar as informações", response = GoldenRaspberryAwardsResponseDTO.class),
                    @ApiResponse(code = 204, message = "Nenhum conteudo encontrado"),
                    @ApiResponse(code = 400, message = "Bad Request"),
                    @ApiResponse(code = 500, message = "Internal Server Error")
            })
    ResponseEntity<GoldenRaspberryAwardsResponseDTO> findGoldenRaspberryAwards(Integer minYear, Integer maxYear);
}
