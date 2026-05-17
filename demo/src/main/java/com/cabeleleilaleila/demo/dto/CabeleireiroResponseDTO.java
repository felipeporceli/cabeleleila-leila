package com.cabeleleilaleila.demo.dto;

import com.cabeleleilaleila.demo.model.enums.EspecialidadeEnum;
import lombok.Data;

@Data
public class CabeleireiroResponseDTO {

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private EspecialidadeEnum especialidade;

}