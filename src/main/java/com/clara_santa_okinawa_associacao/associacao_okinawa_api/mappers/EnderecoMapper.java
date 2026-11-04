package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.endereco.EnderecoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Endereco;
import java.util.Locale;

public class EnderecoMapper {
    public static Endereco toEntity(EnderecoRequest request) {
        Endereco endereco = new Endereco();
        endereco.setRua(limpar(request.rua()));
        endereco.setBairro(limpar(request.bairro()));
        endereco.setCidade(limpar(request.cidade()));
        endereco.setNumero(limpar(request.numero()));
        String uf = limpar(request.uf());
        endereco.setUf(uf == null ? null : uf.toUpperCase(Locale.ROOT));
        endereco.setCep(limpar(request.cep()));
        endereco.setComplemento(limpar(request.complemento()));
        return endereco;
    }

    private static String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
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
