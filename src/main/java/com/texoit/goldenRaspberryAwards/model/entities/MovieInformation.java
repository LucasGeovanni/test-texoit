package com.texoit.goldenRaspberryAwards.model.entities;


import lombok.*;

import javax.persistence.*;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class MovieInformation {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long Id;

    @Column(name = "`YEAR`" )
    private Integer year;  //NON_KEYWORDS=OFFSET

    @Column(name = "TITLE")
    private String title;

    @Column(name = "STUDIOS")
    private String studios;

    @Column(name = "PRODUCERS")
    private String producers;

    @Column(name = "WINNER")
    @Builder.Default
    private Boolean winner = false;



}
