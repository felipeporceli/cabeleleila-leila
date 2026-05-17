package com.cabeleleilaleila.demo.dto;

import lombok.Data;

@Data
public class ClienteResponseDTO {

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String logradouro;
    private String bairro;
    private String numero;
    private String cidade;
    private String complemento;
    private String cep;
}
