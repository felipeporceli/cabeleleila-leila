package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HorarioDisponivelUpdateRequestDTO {

    private DiaSemanaEnum diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer intervaloMinutos;
    private Boolean ativo;

}