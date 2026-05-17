package com.cabeleleilaleila.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
public class ClienteRequestDTO {

    @NotBlank(message = "Campo obrigatorio")
    private String nome;

    @CPF (message = "CPF invalido")
    private String cpf;

    @NotBlank(message = "Campo obrigatorio")
    private String senha;

    @NotBlank(message = "Campo obrigatorio")
    private String email;

    @NotBlank(message = "Campo obrigatorio")
    private String telefone;

    @NotBlank(message = "Campo obrigatorio")
    private String logradouro;

    @NotBlank(message = "Campo obrigatorio")
    private String bairro;

    @NotBlank(message = "Campo obrigatorio")
    private String numero;

    @NotBlank(message = "Campo obrigatorio")
    private String cidade;

    @NotBlank(message = "Campo obrigatorio")
    private String complemento;

    @NotBlank(message = "Campo obrigatorio")
    private String cep;
}
