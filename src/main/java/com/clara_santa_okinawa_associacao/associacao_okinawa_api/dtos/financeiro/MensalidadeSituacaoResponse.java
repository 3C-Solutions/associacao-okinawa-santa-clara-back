package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
public record MensalidadeSituacaoResponse(
        Integer idMensalidade, BigDecimal valor, LocalDate dtVencimento,
        Integer fkDepartamento, String status
) {}
