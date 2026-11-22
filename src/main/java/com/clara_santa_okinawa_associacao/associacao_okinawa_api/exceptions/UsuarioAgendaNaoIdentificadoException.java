package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UsuarioAgendaNaoIdentificadoException extends ResponseStatusException {
    public UsuarioAgendaNaoIdentificadoException() {
        super(HttpStatus.UNAUTHORIZED, "Usuário não identificado");
    }

    public UsuarioAgendaNaoIdentificadoException(String mensagem) {
        super(HttpStatus.UNAUTHORIZED, mensagem);
    }
}