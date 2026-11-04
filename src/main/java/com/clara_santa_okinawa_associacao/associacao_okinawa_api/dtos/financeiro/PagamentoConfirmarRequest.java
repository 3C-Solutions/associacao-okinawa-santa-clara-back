package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record PagamentoConfirmarRequest(@NotNull LocalDate dtPagamento) {}
