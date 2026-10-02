package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

public class NotExistsByIdException extends RuntimeException {
    public NotExistsByIdException() {
    }

    public NotExistsByIdException(String message) {
        super(message);
    }
}
