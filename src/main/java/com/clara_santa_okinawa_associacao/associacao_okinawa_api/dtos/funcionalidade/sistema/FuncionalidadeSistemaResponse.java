package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcionalidade.sistema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Funcionalidade disponível para associação a uma função do sistema")
public record FuncionalidadeSistemaResponse(
        Integer id,
        String nome,
        String descricao,
        String codigo
) {
}
