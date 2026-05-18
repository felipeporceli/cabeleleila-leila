package com.cabeleleilaleila.demo.exception;

public class PagamentoNaoEncontradoException extends RuntimeException {

    public PagamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
