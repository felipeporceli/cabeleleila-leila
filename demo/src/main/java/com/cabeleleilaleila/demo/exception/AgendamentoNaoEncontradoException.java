package com.cabeleleilaleila.demo.exception;

public class AgendamentoNaoEncontradoException extends RuntimeException {

    public AgendamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
