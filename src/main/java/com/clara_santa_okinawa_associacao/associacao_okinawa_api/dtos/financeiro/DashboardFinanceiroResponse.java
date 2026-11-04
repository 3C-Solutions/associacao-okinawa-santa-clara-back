package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.util.List;

public record DashboardFinanceiroResponse(
        ResumoFinanceiroResponse resumo,
        List<LancamentoFinanceiroResponse> lancamentos
) {}
