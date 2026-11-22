package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EventoDadosInvalidosException extends ResponseStatusException {
    public EventoDadosInvalidosException() {
        super(HttpStatus.BAD_REQUEST, "Dados inválidos para o evento");
    }

    public EventoDadosInvalidosException(String mensagem) {
        super(HttpStatus.BAD_REQUEST, mensagem);
    }
}