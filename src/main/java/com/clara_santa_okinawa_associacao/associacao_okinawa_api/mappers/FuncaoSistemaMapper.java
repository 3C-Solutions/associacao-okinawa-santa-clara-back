package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;

public class FuncaoSistemaMapper {

    public static FuncaoSistemaResponse toResponse(FuncaoSistema funcaoSistema) {
        if (funcaoSistema == null) return null;

        FuncaoSistemaResponse dto = new FuncaoSistemaResponse();
        dto.setId(funcaoSistema.getId());
        dto.setNome(funcaoSistema.getNome());
        dto.setDescricao(funcaoSistema.getDescricao());

        return dto;
    }
}
