package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente.AssociadoDependenteRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.AssociadoDependente.AssociadoDependenteResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.AssociadoDependente;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

public class AssociadoDependenteMapper {


    public AssociadoDependente toEntity(AssociadoDependenteRequest dto){
        AssociadoDependente entidade = new AssociadoDependente();

        entidade.setNome(dto.getNome());
        entidade.setEmail(dto.getEmail());
        entidade.setSenha(dto.getSenha());
        entidade.setTelefone(dto.getTelefone());
        entidade.setDataNascimento(dto.getDataNascimento());

        return entidade;
    }

    public AssociadoDependenteResponse toResponse(AssociadoDependente entidade){
        AssociadoDependenteResponse dto = new AssociadoDependenteResponse();

        dto.setId(entidade.getId());
        dto.setNome(entidade.getNome());
        dto.setEmail(entidade.getEmail());
        dto.setTelefone(entidade.getTelefone());
        dto.setDataNascimento(entidade.getDataNascimento());
        dto.setFamilia(FamiliaMapper.toResponse(entidade.getFamilia()));

        return dto;
    }
}
