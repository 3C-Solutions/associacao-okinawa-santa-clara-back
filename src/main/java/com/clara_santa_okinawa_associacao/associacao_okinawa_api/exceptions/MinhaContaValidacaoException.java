package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

public class MinhaContaValidacaoException extends RuntimeException {
    private final String codigo;

    public MinhaContaValidacaoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
