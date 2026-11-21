package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem.ImagemResponse;

import java.time.LocalTime;

public record DepartamentoPublicoResponse(
        Integer id,
        String nome,
        String descricao,
        String diaSemana,
        LocalTime horario,
        String professor,
        ImagemResponse imagem
) {
}