package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;

public record DepartamentoRequest(

        @NotBlank
        String nome,

        String descricao,

        @NotBlank
        String diaSemana,

        @NotNull
        LocalTime horario,

        String professor,

        String escola,

        @PositiveOrZero
        BigDecimal valorMensal,

        @NotNull
        @Positive
        Integer fkResponsavelDepartamento,

        @NotNull
        @Positive
        Integer fkFamilia,


        @NotNull
        @Positive
        Integer quantidadeVagas,

        @NotNull
        Boolean ativo

) {
}