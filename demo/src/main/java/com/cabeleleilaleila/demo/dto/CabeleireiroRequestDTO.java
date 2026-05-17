package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CabeleireiroRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @CPF
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String telefone;

    @NotNull(message = "Especialidade é obrigatória")
    private EspecialidadeEnum especialidade;

}
