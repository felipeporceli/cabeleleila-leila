package com.cabeleleilaleila.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServicoUpdateRequestDTO {

    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String nome;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    private String descricao;

    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @Positive(message = "Duração deve ser maior que zero")
    private Integer duracaoMinutos;

    private Boolean ativo;

}