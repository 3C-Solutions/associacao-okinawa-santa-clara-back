package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Identificação e função atual do usuário autenticado")
public record UsuarioAutenticadoResponse(
        @Schema(description = "Identificador do usuário", example = "1")
        Integer id,

        @Schema(description = "Nome do usuário", example = "João da Silva")
        String nome,

        @Schema(description = "E-mail do usuário", example = "joao@email.com")
        String email,

        @Schema(description = "Função atual do usuário no sistema")
        FuncaoSistemaResponse funcaoSistema
) {
}
