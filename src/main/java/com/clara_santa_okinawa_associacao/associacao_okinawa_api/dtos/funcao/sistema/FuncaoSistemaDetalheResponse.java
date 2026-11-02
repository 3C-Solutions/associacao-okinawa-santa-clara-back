package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcionalidade.sistema.FuncionalidadeSistemaResponse;
import java.util.List;

public record FuncaoSistemaDetalheResponse(
        Integer id, String nome, String descricao,
        List<FuncionalidadeSistemaResponse> funcionalidades
) {
}
