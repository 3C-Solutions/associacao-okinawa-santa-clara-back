package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados devolvidos após a autenticação de um usuário")
public record LoginResponse(
        @Schema(description = "Token JWT utilizado para acessar os endpoints protegidos do sistema",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.assinatura")
        String accesToken,

        @Schema(description = "Tipo do token utilizado no cabeçalho Authorization", example = "Bearer")
        String tokenType,

        @Schema(description = "Tempo de validade do token em segundos", example = "900")
        long expiresIn
) {
}
