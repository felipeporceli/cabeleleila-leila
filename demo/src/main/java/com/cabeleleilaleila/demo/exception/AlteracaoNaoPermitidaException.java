package com.cabeleleilaleila.demo.exception;

public class AlteracaoNaoPermitidaException extends RuntimeException {
    public AlteracaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
