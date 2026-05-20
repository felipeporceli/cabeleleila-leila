package com.cabeleleilaleila.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicoResponseDTO {

    private Integer id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracaoMinutos;
    private Boolean ativo;

}