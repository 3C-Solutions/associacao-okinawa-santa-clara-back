package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EventoConflitoException extends ResponseStatusException {
    public EventoConflitoException() {
        super(HttpStatus.CONFLICT, "Conflito de reserva ou situação inválida");
    }

    public EventoConflitoException(String mensagem) {
        super(HttpStatus.CONFLICT, mensagem);
    }
}