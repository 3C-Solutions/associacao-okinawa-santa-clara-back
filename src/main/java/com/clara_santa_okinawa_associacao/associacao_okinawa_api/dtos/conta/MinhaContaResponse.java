package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.funcao.sistema.FuncaoSistemaResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record MinhaContaResponse(Integer id, String nome, String email, String telefone,
        @JsonFormat(pattern = "dd/MM/yyyy") LocalDate dataNascimento,
        FuncaoSistemaResponse funcaoSistema, FamiliaResponse familia) {
}
