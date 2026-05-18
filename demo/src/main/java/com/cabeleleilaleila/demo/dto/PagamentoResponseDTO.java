package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagamentoResponseDTO {

    private Integer id;
    private Integer agendamentoId;
    private String clienteNome;
    private String cabeleireiroNome;
    private BigDecimal valor;
    private FormaPagamentoEnum formaPagamento;
    private StatusPagamentoEnum statusPagamento;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataPagamento;

    private String observacoes;

}
