package com.cabeleleilaleila.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RelatorioFaturamentoDTO {

    private String cabeleireiroNome;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer totalAtendimentos;
    private BigDecimal totalFaturado;
    private List<PagamentoResponseDTO> pagamentos;

}
