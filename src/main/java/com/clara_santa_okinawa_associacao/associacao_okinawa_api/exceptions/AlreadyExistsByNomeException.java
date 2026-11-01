package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

public class AlreadyExistsByNomeException extends RuntimeException {
    public AlreadyExistsByNomeException(String message) {
        super(message);
    }
}
