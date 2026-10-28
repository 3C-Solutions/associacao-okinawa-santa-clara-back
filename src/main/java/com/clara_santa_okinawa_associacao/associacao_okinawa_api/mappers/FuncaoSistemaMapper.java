package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;

public class FuncaoSistemaMapper {

    public static FuncaoSistemaResponse toResponse(FuncaoSistema funcaoSistema) {
        if (funcaoSistema == null) return null;

        return new FuncaoSistemaResponse(
                funcaoSistema.getId(),
                funcaoSistema.getNome(),
                funcaoSistema.getDescricao()
        );
    }
}
