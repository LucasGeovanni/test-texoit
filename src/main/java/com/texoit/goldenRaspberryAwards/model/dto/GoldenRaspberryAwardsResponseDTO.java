package com.texoit.goldenRaspberryAwards.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoldenRaspberryAwardsResponseDTO {

    private List<AwardsIntervalResponseDTO> min;
    private List<AwardsIntervalResponseDTO> max;
}
