package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.financeiro;
import jakarta.validation.constraints.NotBlank;
public record PagamentoInformarRequest(@NotBlank String forma_pagamento) {}