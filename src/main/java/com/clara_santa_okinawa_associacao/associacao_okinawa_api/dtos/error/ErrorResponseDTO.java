package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resposta padrão de erro da API")
public record ErrorResponseDTO(
        @Schema(description = "Status HTTP do erro", example = "404")
        Integer status,

        @Schema(description = "Mensagem que descreve o erro", example = "Associado não encontrado")
        String mensagem,

        @Schema(description = "Data e horário em que o erro ocorreu", example = "01/10/2026 " +
                "18:00:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime timestamp
) {
}
