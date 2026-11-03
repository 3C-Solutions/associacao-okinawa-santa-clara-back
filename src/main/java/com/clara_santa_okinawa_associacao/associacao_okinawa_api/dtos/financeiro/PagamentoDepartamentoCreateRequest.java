package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.*;
public record PagamentoDepartamentoCreateRequest(
        @NotNull Integer fkDepartamento,
        @NotNull Integer fkMensalidade,
        @NotBlank String formaPagamento
) {}
