package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record MinhaContaRequest(
        @NotBlank @Size(min = 3, max = 255) String nome,
        @NotBlank @Email @Size(max = 255) String email,
        @Size(max = 20) String telefone,
        @Size(max = 255) @Schema(accessMode = Schema.AccessMode.WRITE_ONLY,
                description = "Obrigatória somente ao alterar o e-mail") String senhaAtual
) {
    public MinhaContaRequest {
        nome = nome == null ? null : nome.trim();
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        telefone = telefone == null || telefone.isBlank() ? null : telefone.trim();
    }
}
