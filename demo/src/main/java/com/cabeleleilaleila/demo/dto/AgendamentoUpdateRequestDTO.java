package com.cabeleleilaleila.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendamentoUpdateRequestDTO {

    private Integer clienteId;

    private Integer cabeleireiroId;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataAgendamento;

    private String observacoes;

}
