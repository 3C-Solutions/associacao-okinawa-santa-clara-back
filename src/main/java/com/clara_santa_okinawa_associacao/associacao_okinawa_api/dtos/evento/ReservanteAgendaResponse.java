package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento;

import java.util.List;

public record ReservantesAgendaResponse(
        List<OpcaoReservante> departamentos,
        List<OpcaoReservante> familias
) {
    public record OpcaoReservante(
            Integer id,
            String nome
    ) {
    }
}

