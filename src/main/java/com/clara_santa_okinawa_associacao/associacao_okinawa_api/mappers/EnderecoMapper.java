package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;

public class EnderecoMapper {

    public static EnderecoResponse toResponse(Endereco endereco) {
        if (endereco == null) return null;

        return new EnderecoResponse(
                endereco.getId(),
                endereco.getRua(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getNumero(),
                endereco.getUf(),
                endereco.getCep(),
                endereco.getComplemento()
        );
    }
}
