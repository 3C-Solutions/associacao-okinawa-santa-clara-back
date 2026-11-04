package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.math.BigDecimal;

public record ResumoFinanceiroResponse(
        BigDecimal saldoMensal, BigDecimal saldoAnual,
        BigDecimal entradasAnuais, BigDecimal saidasAnuais,
        BigDecimal variacaoMensal
) {}
