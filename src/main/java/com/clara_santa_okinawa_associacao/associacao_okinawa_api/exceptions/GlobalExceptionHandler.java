package com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleDadosInvalidos(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, mensagem, LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponseDTO(400, "Corpo da requisição ausente ou JSON inválido", LocalDateTime.now()));
    }

    @ExceptionHandler(AlreadyExistsByNomeException.class)
    public ResponseEntity<ErrorResponseDTO> handleNomeJaCadastrado(AlreadyExistsByNomeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponseDTO(409, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleFalhaAutenticacao(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponseDTO(
                        401,
                        "E-mail ou senha inválidos ou usuário desativado",
                        LocalDateTime.now()
                )
        );
    }

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
