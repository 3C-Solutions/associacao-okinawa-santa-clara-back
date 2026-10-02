package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

public class AlreadyExistsByEmailException extends RuntimeException {
    public AlreadyExistsByEmailException(String message) {
        super(message);
    }
}
