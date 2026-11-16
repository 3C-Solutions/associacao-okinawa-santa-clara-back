package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.matricula;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record MatriculaRequest(

        @NotNull
        @Positive
        Integer fkUsuario,

        @NotNull
        @Positive
        Integer fkFamilia,

        @NotNull
        @Positive
        Integer fkDepartamento,

        @NotNull
        LocalDate dataInicioMatricula,

        @NotNull
        Boolean ativo

) {
}