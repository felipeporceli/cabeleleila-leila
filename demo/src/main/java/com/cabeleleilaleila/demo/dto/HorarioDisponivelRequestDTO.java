package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.DiaSemanaEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioDisponivelRequestDTO {

    @NotNull(message = "Cabeleireiro é obrigatório")
    private Integer cabeleireiroId;

    @NotNull(message = "Dia da semana é obrigatório")
    private DiaSemanaEnum diaSemana;

    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime horaFim;

    @NotNull(message = "Intervalo em minutos é obrigatório")
    private Integer intervaloMinutos;

    private Boolean ativo = true;

}
