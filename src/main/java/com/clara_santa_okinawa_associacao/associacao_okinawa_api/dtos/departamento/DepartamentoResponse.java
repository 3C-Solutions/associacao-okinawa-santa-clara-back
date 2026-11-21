package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.departamento;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.imagem.ImagemResponse;

import java.math.BigDecimal;
import java.time.LocalTime;

public record DepartamentoResponse(

        Integer id,
        String nome,
        String descricao,
        String diaSemana,
        LocalTime horario,
        String professor,
        String escola,
        BigDecimal valorMensal,

        Integer fkResponsavelDepartamento,
        Integer fkFamilia,

        ImagemResponse imagem,

        Integer quantidadeVagas,
        Boolean ativo

) {
}