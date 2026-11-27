package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoReembolsoResponse(
        Integer id,
        String solicitante,
        String funcaoSolicitante,
        String motivo,
        BigDecimal valor,
        String status,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataRevisao,
        String motivoRecusa,
        ComprovanteReembolsoResponse comprovante
) {}
