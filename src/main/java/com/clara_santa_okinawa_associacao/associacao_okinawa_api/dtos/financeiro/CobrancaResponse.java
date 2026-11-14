package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import java.math.BigDecimal;
import java.time.LocalDate;
public record CobrancaResponse(Integer id_cobranca, String nome_da_cobranca,
                               String periodo, BigDecimal valor, LocalDate vencimento,
                               long quantidade_destinatarios, long pagamentos_recebidos) {}