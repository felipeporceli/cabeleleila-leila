package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioDisponivelResponseDTO {

    private Integer id;
    private Integer cabeleireiroId;
    private String cabeleireiroNome;
    private DiaSemanaEnum diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer intervaloMinutos;
    private Boolean ativo;

}
