package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CabeleireiroUpdateRequestDTO {

    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String nome;

    @Size(min = 11, max = 15, message = "CPF inválido")
    private String cpf;

    @Size(min = 6, max = 250, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Email(message = "Email inválido")
    @Size(max = 200, message = "Email deve ter no máximo 200 caracteres")
    private String email;

    @Size(max = 25, message = "Telefone deve ter no máximo 25 caracteres")
    private String telefone;

    private EspecialidadeEnum especialidade;

}
