
package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import java.math.BigDecimal;
import java.time.LocalDate;
public record PagamentoCobrancaResponse(
        Integer id_pagamento_cobranca, Integer id_cobranca, Integer fk_usuario,
        Integer fk_familia, String destinatario, String nome_da_cobranca,
        String periodo, BigDecimal valor, LocalDate vencimento,
        String status, String forma_pagamento) {}