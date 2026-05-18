package com.cabeleleilaleila.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgendamentoRequestDTO {

    @NotNull(message = "Cliente é obrigatório")
    private Integer clienteId;

    @NotNull(message = "Cabeleireiro é obrigatório")
    private Integer cabeleireiroId;

    @NotNull(message = "Data do agendamento é obrigatória")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataAgendamento;

    private String observacoes;

}
