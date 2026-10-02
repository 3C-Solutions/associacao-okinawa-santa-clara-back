package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    @ExceptionHandler(AlreadyExistsByEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailJaCadastrado(AlreadyExistsByEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponseDTO(
                        409,
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(NotExistsByIdException.class)
    public ResponseEntity<ErrorResponseDTO> handleIdNaoEncontrado(NotExistsByIdException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDTO(
                        404,
                        ex.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

}
