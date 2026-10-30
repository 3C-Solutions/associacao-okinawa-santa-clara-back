package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({
        "id",
        "rua",
        "bairro",
        "cidade",
        "numero",
        "uf",
        "cep",
        "complemento"
})

@Schema(description = "Dados responsáveis por devolver uma resposta de uma requisição")
public record EnderecoResponse(
        @Schema(description = "Identificador único do endereco", example = "1")
        Integer id,

        @Schema(description = "Nome da rua")
        String rua,

        @Schema(description = "Nome do bairro")
        String bairro,

        @Schema(description = "Nome da cidade")
        String cidade,

        @Schema(description = "Número da rua")
        String numero,

        @Schema(description = "Unidade Federativa", example = "SP")
        String uf,

        @Schema(description = "CEP")
        String cep,

        @Schema(description = "Complemento")
        String complemento
) {
}
