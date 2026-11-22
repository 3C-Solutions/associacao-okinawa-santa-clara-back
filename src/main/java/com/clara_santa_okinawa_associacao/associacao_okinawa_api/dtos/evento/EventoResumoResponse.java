package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento;
import java.time.LocalDate;
import java.time.LocalTime;

// Apenas informações que todos os perfis autenticados podem consultar.
public record EventoResumoResponse(
        Integer id,
        LocalDate dtInicio, LocalDate dtFim,
        Boolean diaInteiro, LocalTime horaInicio, LocalTime horaFim,
        String reservadoPara
) {}