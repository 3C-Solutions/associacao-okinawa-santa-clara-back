package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesasUpdateRequest(
        @NotNull LocalDate dtDepesa,
        @NotBlank @Size(max = 200) String descricao,
        @NotBlank @Size(max = 100) String categoria,
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @NotBlank String status,
        @NotNull Integer fkUsuario,
        Integer fkFamilia
) {}
