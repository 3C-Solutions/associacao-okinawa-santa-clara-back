package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
public record DespesasResponse(
        Integer idDespesas, LocalDate dtDepesa, String descricao,String categoria, BigDecimal valor, String status,
        Integer fkUsuario, Integer fkFamilia
) {}