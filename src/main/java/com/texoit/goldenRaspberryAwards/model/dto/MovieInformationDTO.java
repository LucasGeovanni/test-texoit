package com.texoit.goldenRaspberryAwards.model.dto;

import lombok.*;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieInformationDTO {

    private Integer year;
    private String title;
    private String studios;
    private String producers;
    private Boolean winner;

}
