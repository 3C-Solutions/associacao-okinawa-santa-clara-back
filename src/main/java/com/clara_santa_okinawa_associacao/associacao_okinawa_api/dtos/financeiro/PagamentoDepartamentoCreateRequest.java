package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.*;
public record PagamentoDepartamentoCreateRequest(
        @NotNull @Positive Integer fkDepartamento,
        @NotNull @Positive Integer fkMensalidade,
        @NotBlank @Size(max = 100) String formaPagamento
) {}
