package com.texoit.goldenRaspberryAwards.model.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardsIntervalResponseDTO {

    private String producer;
    private Integer interval;
    private Integer previousWin;
    private Integer followingWin;
}
