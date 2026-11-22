package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.evento;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.EspacoEvento;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventoRequest(
        @NotBlank @Size(min = 3, max = 100) String titulo,
        @Size(max = 500) String descricao,
        @NotNull LocalDate dtInicio,
        @NotNull LocalDate dtFim,
        @NotNull Boolean diaInteiro,
        LocalTime horaInicio,
        LocalTime horaFim,
        @NotNull EspacoEvento codigoEspaco,
        @Size(max = 100) String local,
        @Positive Integer fkDepartamento,
        @Positive Integer fkFamilia
) {}