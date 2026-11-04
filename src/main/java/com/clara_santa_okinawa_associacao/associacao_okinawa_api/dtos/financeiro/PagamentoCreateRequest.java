package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.*;
public record PagamentoCreateRequest(
        @NotNull @Positive Integer fkFamilia,
        @NotNull @Positive Integer fkAnuidade,
        @NotBlank @Size(max = 100) String formaPagamento
) {}
