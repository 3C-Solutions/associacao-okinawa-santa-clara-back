package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

@Schema(description = "Dados utilizados para atualização de um usuário")
public record UsuarioUpdateRequest(
        @NotBlank
        @Schema(description = "Nome completo do usuário", example = "João Silva")
        String nome,

        @Nullable
        @Schema(description = "Telefone do usuário", example = "11999999999")
        String telefone,

        @Schema(description = "Status do usuário no sistema", example = "true")
        boolean ativo,

        @NotNull
        @Schema(description = "Identificador único da função que o usuário exerce no sistema",
                example = "1")
        Integer idFuncaoSistema
) {
}
