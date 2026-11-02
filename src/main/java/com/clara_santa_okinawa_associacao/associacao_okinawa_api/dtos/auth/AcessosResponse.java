package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Telas permitidas ao usuário autenticado")
public record AcessosResponse(
        @Schema(description = "Códigos únicos das permissões TELA_, em ordem alfabética; lista vazia quando não há telas permitidas",
                example = "[\"TELA_FUNCOES_SISTEMA\", \"TELA_USUARIOS\"]")
        List<String> telas
) {
}
