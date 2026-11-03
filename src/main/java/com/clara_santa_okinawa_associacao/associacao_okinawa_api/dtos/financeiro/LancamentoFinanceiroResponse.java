package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
// DTO de consolidacao; nao existe tabela LancamentoFinanceiro no DER.
public record LancamentoFinanceiroResponse(
        String id, LocalDate data, String descricao, String categoria,
        String tipo, BigDecimal valor, String status, boolean bloqueado,
        Integer fkUsuario, Integer fkFamilia
) {}
