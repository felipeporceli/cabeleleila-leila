package com.cabeleleilaleila.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendamentoUpdateRequestDTO {

    private Integer clienteId;

    private Integer cabeleireiroId;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataAgendamento;

    private List<Integer> servicoIds;

    private String observacoes;

}
