package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Dados para cadastrar uma função e suas permissões no sistema")
public record FuncaoSistemaCreateRequest(
        @NotBlank
        @Size(max = 100)
        @Schema(description = "Nome único da função", example = "Diretoria")
        String nome,

        @Size(max = 255)
        @Schema(description = "Descrição opcional da função", example = "Administração da associação")
        String descricao,

        @NotEmpty
        @Schema(description = "IDs das funcionalidades existentes que a função poderá acessar", example = "[1, 2]")
        Set<@NotNull @Positive Integer> idsFuncionalidades
) {
}
