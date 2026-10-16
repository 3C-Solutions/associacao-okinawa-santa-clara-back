package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.familia.FamiliaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;

public class FamiliaMapper {

    public static FamiliaResponse toResponse(Familia familia) {
        if (familia == null) return null;

        FamiliaResponse dto = new FamiliaResponse();
        dto.setId(familia.getId());
        dto.setNome(familia.getNome());
        dto.setEndereco(EnderecoMapper.toResponse(familia.getEndereco()));

        return dto;
    }
}
