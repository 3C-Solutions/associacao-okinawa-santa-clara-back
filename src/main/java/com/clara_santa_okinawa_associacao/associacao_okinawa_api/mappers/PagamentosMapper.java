package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.PagamentoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro.PagamentoDepartamentoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Pagamento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.PagamentoDepartamento;
public final class PagamentosMapper {
    private PagamentosMapper() {}
    public static PagamentoResponse toResponse(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getIdPagamento(),
                pagamento.getFkFamilia(),
                pagamento.getFkAnuidade(),
                pagamento.getDtPagamento(),
                pagamento.getFormaPagamento(),
                pagamento.getStatus()
        );
    }
    public static PagamentoDepartamentoResponse toResponse(
            PagamentoDepartamento pagamento) {
        return new PagamentoDepartamentoResponse(
                pagamento.getIdPagamentoDepartamento(),
                pagamento.getFkDepartamento(),
                pagamento.getFkMensalidade(),
                pagamento.getDtPagamento(),
                pagamento.getFormaPagamento(),
                pagamento.getStatus()
        );
    }
}
