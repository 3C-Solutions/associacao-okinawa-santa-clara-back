package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados utilizados para autenticação de um usuário")
public record LoginRequest(
        @NotBlank
        @Email
        @Schema(description = "E-mail do usuário cadastrado no sistema", example = "joao@email.com")
        String email,

        @NotBlank
        @Schema(description = "Senha do usuário cadastrado no sistema", example = "Senha123!")
        String senha
) {
}
