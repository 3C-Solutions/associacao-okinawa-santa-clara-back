package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento.*;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.*;

public final class EventoMapper {
    private EventoMapper() {}

    public static void preencher(Evento evento, EventoRequest request) {
        evento.setTitulo(request.titulo().trim());
        evento.setDescricao(request.descricao() == null ? null : request.descricao().trim());
        evento.setDtInicio(request.dtInicio());
        evento.setDtFim(request.dtFim());
        evento.setDiaInteiro(request.diaInteiro());
        evento.setHoraInicio(Boolean.TRUE.equals(request.diaInteiro()) ? null : request.horaInicio());
        evento.setHoraFim(Boolean.TRUE.equals(request.diaInteiro()) ? null : request.horaFim());
        evento.setCodigoEspaco(request.codigoEspaco());
        evento.setLocal(request.codigoEspaco().getDescricao());
    }

    public static EventoResumoResponse toResumo(Evento evento, String reservadoPara) {
        return new EventoResumoResponse(evento.getId(), evento.getDtInicio(), evento.getDtFim(),
                evento.getDiaInteiro(), evento.getHoraInicio(), evento.getHoraFim(), reservadoPara);
    }

    public static EventoResponse toResponse(Evento evento, String reservadoPara) {
        return new EventoResponse(evento.getId(), evento.getTitulo(), evento.getDescricao(),
                evento.getDtInicio(), evento.getDtFim(), evento.getDiaInteiro(),
                evento.getHoraInicio(), evento.getHoraFim(), evento.getCodigoEspaco(),
                evento.getLocal(), reservadoPara, evento.getStatus(), evento.getOrigemCadastro(),
                evento.getFkUsuario(), evento.getFkFamilia(), evento.getFkDepartamento(),
                evento.getMotivoRecusa(), evento.getStatusSincronizacao());
    }
}