package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;

public record FuncaoSistemaFuncionalidadesRequest(
        @NotNull Set<@NotNull @Positive Integer> idsFuncionalidades
) {
}
