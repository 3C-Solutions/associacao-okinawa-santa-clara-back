package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import java.util.List;
public record DashboardFinanceiroConsolidadoResponse(
        ResumoFinanceiroConsolidadoResponse resumo,
        List<LancamentoResponse> lancamentos
) {}