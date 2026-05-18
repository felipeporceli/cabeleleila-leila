package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.StatusAgendamentoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgendamentoResponseDTO {

    private Integer id;
    private Integer clienteId;
    private String clienteNome;
    private Integer cabeleireiroId;
    private String cabeleireiroNome;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataAgendamento;

    private String observacoes;
    private StatusAgendamentoEnum statusAgendamento;
    private String sugestao;

}
