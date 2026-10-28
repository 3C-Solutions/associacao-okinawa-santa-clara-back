package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados utilizados para realizar o cadastro de um associado")
public record UsuarioCreateRequest(
        @NotNull
        @Schema(description = "Identificador único da família no qual o usuário representa",
                example = "2")
        Integer idFamilia,

        @NotBlank
        @Schema(description = "Nome completo do associado a ser cadastrado", example = "João da " +
                "Silva")
        String nome,

        @NotBlank
        @Email
        @Schema(description = "E-mail do associado a ser cadastrado", example = "joao@email.com")
        String email,

        @Nullable
        @Size(max = 20)
        String telefone,

        @NotNull
        @Past
        @Schema(description = "Data de nascimento do associado a ser cadastrado, sempre no passado",
                example = "19/09/1999")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @NotNull
        @Schema(description = "Identificador único da função que o usuário tem no sistema",
                example = "1")
        Integer idFuncaoSistema
) {

}
