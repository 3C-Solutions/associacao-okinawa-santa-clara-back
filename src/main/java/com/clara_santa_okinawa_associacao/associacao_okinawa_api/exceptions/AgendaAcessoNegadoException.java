package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AgendaAcessoNegadoException extends ResponseStatusException {
    public AgendaAcessoNegadoException() {
        super(HttpStatus.FORBIDDEN, "Acesso à agenda não autorizado");
    }

    public AgendaAcessoNegadoException(String mensagem) {
        super(HttpStatus.FORBIDDEN, mensagem);
    }
}