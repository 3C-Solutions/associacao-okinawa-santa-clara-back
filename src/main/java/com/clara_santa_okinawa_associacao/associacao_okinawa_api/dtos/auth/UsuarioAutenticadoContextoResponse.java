package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Usuário autenticado e seus acessos atuais")
public record UsuarioAutenticadoContextoResponse(
        UsuarioAutenticadoResponse usuario,

        @Schema(description = "Códigos das permissões de acesso às telas concedidas à função do usuário, únicos e em ordem alfabética; lista vazia quando não há acessos",
                example = "[\"TELA_USUARIOS\"]")
        List<String> permissoes
) {
}
