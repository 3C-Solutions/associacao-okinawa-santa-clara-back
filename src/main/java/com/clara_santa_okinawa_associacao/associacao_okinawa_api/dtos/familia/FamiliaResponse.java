package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({
        "id",
        "nome",
        "endereco"
})

@Schema(description = "Dados que serão devolvidos como resposta de um requisição")
public record FamiliaResponse(
        @Schema(description = "Identificador único da família", example = "1")
        Integer id,

        @Schema(description = "Nome da família", example = "Silva")
        String nome,

        @Schema(description = "Dados do endereço da família")
        EnderecoResponse endereco
) {
}
