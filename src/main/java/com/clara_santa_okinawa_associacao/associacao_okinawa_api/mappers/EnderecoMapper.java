package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;

public class EnderecoMapper {

    public static EnderecoResponse toResponse(Endereco endereco) {
        if (endereco == null) return null;

        EnderecoResponse dto = new EnderecoResponse();
        dto.setId(endereco.getId());
        dto.setRua(endereco.getRua());
        dto.setBairro(endereco.getBairro());
        dto.setCep(endereco.getCep());
        dto.setCidade(endereco.getCidade());
        dto.setComplemento(endereco.getComplemento());
        dto.setUf(endereco.getUf());
        dto.setNumero(endereco.getNumero());

        return dto;
    }
}
