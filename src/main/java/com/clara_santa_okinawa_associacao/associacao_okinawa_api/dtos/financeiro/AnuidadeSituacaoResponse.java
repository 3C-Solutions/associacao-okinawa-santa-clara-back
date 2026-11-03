package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnuidadeSituacaoResponse(
        Integer idAnuidade, Integer ano, BigDecimal valor,
        LocalDate dtVencimento, String status
) {}
