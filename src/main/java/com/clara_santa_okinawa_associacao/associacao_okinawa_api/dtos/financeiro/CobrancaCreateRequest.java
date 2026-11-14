package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record CobrancaCreateRequest(
        @NotBlank String nome_da_cobranca,
        @NotBlank String periodo,
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @NotNull LocalDate vencimento,
        @NotBlank String forma_selecao,
        List<String> grupos_destinatarios,
        List<Integer> usuarios_especificos
) {}
