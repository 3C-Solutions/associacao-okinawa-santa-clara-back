package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({
        "id",
        "nome",
        "descricao"
})

@Schema(description = "Dados que representam a resposta de uma requisição")
public record FuncaoSistemaResponse(
        @Schema(description = "Identificador único de uma função do sistema", example = "2")
        Integer id,

        @Schema(description = "Nome da função", example = "Diretoria")
        String nome,

        @Schema(description = "Descrição da função", example = "Função responsável por toda a parte " +
                "administrativa do sistema")
        String descricao
) {
}
