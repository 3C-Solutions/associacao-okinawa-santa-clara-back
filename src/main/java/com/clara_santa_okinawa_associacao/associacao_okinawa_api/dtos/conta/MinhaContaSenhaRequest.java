package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MinhaContaSenhaRequest(
        @NotBlank @Size(max = 255) @Schema(accessMode = Schema.AccessMode.WRITE_ONLY) String senhaAtual,
        @NotBlank @Size(min = 8, max = 72) @Schema(accessMode = Schema.AccessMode.WRITE_ONLY,
                description = "Nova senha, de 8 caracteres até 72 bytes em UTF-8") String novaSenha
) {
}
