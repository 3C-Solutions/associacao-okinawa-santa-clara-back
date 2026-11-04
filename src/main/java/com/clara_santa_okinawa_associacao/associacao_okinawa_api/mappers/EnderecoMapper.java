package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;

public class EnderecoMapper {
    public static Endereco toEntity(EnderecoRequest request) {
        Endereco endereco = new Endereco();
        endereco.setRua(request.rua());
        endereco.setBairro(request.bairro());
        endereco.setCidade(request.cidade());
        endereco.setNumero(request.numero());
        endereco.setUf(request.uf());
        endereco.setCep(request.cep());
        endereco.setComplemento(request.complemento());
        return endereco;
    }

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
