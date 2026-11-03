package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.time.LocalDate;
public record PagamentoResponse(
        Integer idPagamento, Integer fkFamilia, Integer fkAnuidade,
        LocalDate dtPagamento, String formaPagamento, String status
) {}
