package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EventoNaoEncontradoException extends ResponseStatusException {
    public EventoNaoEncontradoException() {
        super(HttpStatus.NOT_FOUND, "Evento não encontrado");
    }

    public EventoNaoEncontradoException(String mensagem) {
        super(HttpStatus.NOT_FOUND, mensagem);
    }
}