package com.cabeleleilaleila.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class RelatorioSemanalDTO {

    private String cabeleireiroMaisAtendimentos;
    private String diaMaisAtendimentos;
    private Integer totalAgendamentos;
    private Integer totalConfirmados;
    private Integer totalCancelados;
    private Integer totalConcluidos;
    private BigDecimal totalFaturado;
    private Map<String, BigDecimal> faturamentoPorFormaPagamento;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataInicio;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataFim;

}