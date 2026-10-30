package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonPropertyOrder({
        "id",
        "nome",
        "email",
        "telefone",
        "dataNascimento",
        "ativo",
        "familia",
        "funcaoSistema",
        "dataCriacao"
})

@Schema(description = "Dados que serão devolvidos como resposta de um requisição")
public record UsuarioResponse(
        @Schema(description = "Identificador único do associado", example = "1")
        Integer id,

        @Schema(description = "Nome completo do usuário", example = "João da Silva")
        String nome,

        @Schema(description = "E-mail do usuário", example = "joao@email.com")
        String email,

        @Schema(description = "Telefone do usuário", example = "(11) 99999-9999")
        String telefone,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(description = "Data de nascimento do usuário", example = "19/09/1999")
        LocalDate dataNascimento,

        @Schema(description = "Status do usuário no sistema", example = "true")
        boolean ativo,

        @Schema(description = "Função que o usuário exerce dentro do sistema")
        FuncaoSistemaResponse funcaoSistema,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @Schema(description = "Data de criação do usuário no sistema", example = "30/09/2026")
        LocalDateTime dataCriacao,

        @Schema(description = "Família a qual o usuário representa na associação")
        FamiliaResponse familia
) {
}
