package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.EspacoEvento;
import java.time.LocalDate;
import java.time.LocalTime;

// Completo para Diretoria e para o AUTOR da própria solicitação.
public record EventoResponse(
        Integer id, String titulo, String descricao,
        LocalDate dtInicio, LocalDate dtFim, Boolean diaInteiro,
        LocalTime horaInicio, LocalTime horaFim,
        EspacoEvento codigoEspaco, String local,
        String reservadoPara, String status, String origemCadastro,
        Integer fkUsuario, Integer fkFamilia, Integer fkDepartamento,
        String motivoRecusa, String statusSincronizacao
) {}