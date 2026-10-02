package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoCreateRequestDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoResponseDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.associado.AssociadoUpdateRequestDTO;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Associado;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssociadoMapper {
    public AssociadoResponseDTO toResponseDto(Associado associado) {
        if (associado == null) return null;

        AssociadoResponseDTO dto = new AssociadoResponseDTO();
        dto.setId(associado.getId());
        dto.setNomeCompleto(associado.getNomeCompleto());
        dto.setDataNascimento(associado.getDataNascimento());
        dto.setEmail(associado.getEmail());
        dto.setEstadoCivil(associado.getEstadoCivil());
        dto.setAssociado(associado.getAssociado());
        dto.setFilhosCadastrados(associado.getFilhosCadastrados());
        dto.setDataCriacao(associado.getDataCriacao());
        return dto;
    }

    public List<AssociadoResponseDTO> toResponseDto(List<Associado> associados) {
        return associados.stream().map(this::toResponseDto).toList();
    }

    public Associado toEntity(AssociadoCreateRequestDTO dto) {
        Associado associado = new Associado();
        associado.setNomeCompleto(dto.getNomeCompleto());
        associado.setDataNascimento(dto.getDataNascimento());
        associado.setEmail(dto.getEmail());
        associado.setEstadoCivil(dto.getEstadoCivil());
        return associado;
    }

    public Associado toEntity(AssociadoUpdateRequestDTO dto) {
        Associado associado = new Associado();
        associado.setNomeCompleto(dto.getNomeCompleto());
        associado.setDataNascimento(dto.getDataNascimento());
        associado.setEmail(dto.getEmail());
        associado.setEstadoCivil(dto.getEstadoCivil());
        associado.setFilhosCadastrados(dto.getFilhosCadastrados());
        associado.setAssociado(dto.isAssociado());
        return associado;
    }

    public void updateEntity(AssociadoUpdateRequestDTO dto, Associado associado) {
        associado.setNomeCompleto(dto.getNomeCompleto());
        associado.setDataNascimento(dto.getDataNascimento());
        associado.setEmail(dto.getEmail());
        associado.setEstadoCivil(dto.getEstadoCivil());
        associado.setFilhosCadastrados(dto.getFilhosCadastrados());
        associado.setAssociado(dto.isAssociado());
    }
}
