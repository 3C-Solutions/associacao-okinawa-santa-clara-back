package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Cadastro conjunto de família e endereço, realizado em uma única transação")
public record FamiliaComEnderecoRequest(
        @NotBlank
        @Size(min = 2, max = 255)
        @Schema(description = "Sobrenome da família", example = "Takara", requiredMode = Schema.RequiredMode.REQUIRED)
        String nome,

        @Valid
        @NotNull
        @Schema(description = "Endereço completo a cadastrar", requiredMode = Schema.RequiredMode.REQUIRED)
        EnderecoRequest endereco
) {
    public FamiliaComEnderecoRequest {
        nome = nome == null ? null : nome.trim();
    }
}
