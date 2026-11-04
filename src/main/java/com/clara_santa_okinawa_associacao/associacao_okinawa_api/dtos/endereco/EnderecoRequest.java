package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar um endereço")
public record EnderecoRequest(
        @NotBlank
        @Size(max = 255)
        @Schema(description = "Logradouro do endereço", example = "Rua das Flores", requiredMode = Schema.RequiredMode.REQUIRED)
        String rua,

        @Size(max = 255)
        @Schema(description = "Bairro", example = "Liberdade")
        String bairro,

        @Size(max = 255)
        @Schema(description = "Cidade", example = "São Paulo")
        String cidade,

        @Size(max = 255)
        @Schema(description = "Número do endereço", example = "245")
        String numero,

        @Pattern(regexp = "^$|^[a-zA-Z]{2}$", message = "deve conter a sigla de UF com duas letras")
        @Schema(description = "Unidade Federativa", example = "SP")
        String uf,

        @Pattern(regexp = "^$|^\\d{5}-?\\d{3}$", message = "deve conter um CEP válido")
        @Schema(description = "CEP com ou sem hífen", example = "01504-001")
        String cep,

        @Size(max = 255)
        @Schema(description = "Complemento do endereço", example = "Casa 2")
        String complemento
) {
}