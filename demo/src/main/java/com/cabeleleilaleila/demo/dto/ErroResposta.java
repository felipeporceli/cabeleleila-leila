package com.cabeleleilaleila.demo.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
public record ErroResposta (Integer status, String mensagem, List<ErroCampo> erros) {

    /* Metodo que retorna um objeto do tipo ErroResposta que contém o código 400 (bad request). Vamos utilizar esse
    metodo para retornamos ao consumidor da API um erro padrão */
    public static ErroResposta respostaPadrao (String mensagem) {
        return new ErroResposta(HttpStatus.BAD_REQUEST.value(), mensagem, List.of());
    }


    /* Metodo que retorna um objeto do tipo ErroResposta que contém o código 409 (Conflict). Vamos utilizar esse
   metodo para retornamos ao consumidor quando uma entidade já existir */
    public static ErroResposta respostaConflito (String mensagem) {
        return new ErroResposta(HttpStatus.CONFLICT.value(), mensagem, List.of());
    }
}
