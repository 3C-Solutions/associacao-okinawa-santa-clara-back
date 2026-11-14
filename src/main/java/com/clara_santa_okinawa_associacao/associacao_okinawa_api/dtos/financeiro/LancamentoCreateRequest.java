package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record LancamentoCreateRequest(
        @NotNull LocalDate dt_lancamento,
        @NotBlank String tipo_lancamento,
        @NotBlank String descricao,
        @NotBlank String categoria,
        @NotNull @DecimalMin("0.01") @Digits(integer=12, fraction=2) BigDecimal valor,
        @NotBlank String status,
        Integer fk_familia
) {}