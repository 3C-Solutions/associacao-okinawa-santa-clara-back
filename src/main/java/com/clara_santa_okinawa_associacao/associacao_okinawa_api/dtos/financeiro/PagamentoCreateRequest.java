package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.*;
public record PagamentoCreateRequest(
        @NotNull Integer fkFamilia,
        @NotNull Integer fkAnuidade,
        @NotBlank String formaPagamento
) {}
