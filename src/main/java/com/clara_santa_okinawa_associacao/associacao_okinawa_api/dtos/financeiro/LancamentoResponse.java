package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import java.math.BigDecimal;
import java.time.LocalDate;
public record LancamentoResponse(String id, LocalDate dt_lancamento,
                                 String descricao, String categoria, String tipo_lancamento,
                                 BigDecimal valor, String status, boolean bloqueado,
                                 Integer fk_usuario, Integer fk_familia) {}