package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento;
import jakarta.validation.constraints.Size;

public record RecusaEventoRequest(
        @Size(max = 500) String motivoRecusa) {}