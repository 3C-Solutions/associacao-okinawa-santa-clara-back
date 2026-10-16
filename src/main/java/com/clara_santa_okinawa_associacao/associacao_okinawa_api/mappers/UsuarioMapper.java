package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioUpdateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;

import java.util.List;

public class UsuarioMapper {
    public static UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioResponse dto = new UsuarioResponse();
        dto.setId(usuario.getId());
        dto.setFamilia(FamiliaMapper.toResponse(usuario.getFamilia()));
        dto.setNome(usuario.getNome());
        dto.setDataNascimento(usuario.getDataNascimento());
        dto.setEmail(usuario.getEmail());
        dto.setAtivo(usuario.isAtivo());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setTelefone(usuario.getTelefone());
        dto.setFuncaoSistema(FuncaoSistemaMapper.toResponse(usuario.getFuncaoSistema()));
        return dto;
    }

    public static List<UsuarioResponse> toResponse(List<Usuario> associados) {
        return associados.stream().map(UsuarioMapper::toResponse).toList();
    }

    public static Usuario toEntity(UsuarioCreateRequest dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setTelefone(dto.getTelefone());
        usuario.setAtivo(true);
        return usuario;
    }

    public static void updateEntity(UsuarioUpdateRequest dto, Usuario usuario) {
        usuario.setNome(dto.getNome());
        usuario.setTelefone(dto.getTelefone());
        usuario.setFamilia(usuario.getFamilia());
        usuario.setAtivo(dto.isAtivo());
    }
}
