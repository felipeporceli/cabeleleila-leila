package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.FormaPagamentoEnum;
import com.cabeleleilaleila.demo.model.enums.StatusPagamentoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagamentoUpdateRequestDTO {

    @Positive(message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    private FormaPagamentoEnum formaPagamento;

    private StatusPagamentoEnum statusPagamento;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataPagamento;

    private String observacoes;

}
