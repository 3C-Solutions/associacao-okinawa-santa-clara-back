package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.time.LocalDate;
public record PagamentoDepartamentoResponse(
        Integer idPagamentoDepartamento, Integer fkDepartamento, Integer fkMensalidade,
        LocalDate dtPagamento, String formaPagamento, String status
) {}
