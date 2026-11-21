package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.matricula;

import java.time.LocalDate;

public record MatriculaResponse(

        Integer id,
        Integer fkUsuario,
        Integer fkFamilia,
        Integer fkDepartamento,
        LocalDate dataInicioMatricula,
        Boolean ativo

) {
}