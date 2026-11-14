package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import java.math.BigDecimal;
public record ResumoFinanceiroConsolidadoResponse(BigDecimal saldo_mensal, BigDecimal saldo_anual, BigDecimal entradas_anuais, BigDecimal saidas_anuais, BigDecimal variacao_mensal) {}