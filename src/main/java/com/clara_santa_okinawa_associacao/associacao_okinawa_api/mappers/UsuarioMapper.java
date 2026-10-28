package com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioUpdateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;

import java.util.List;

public class UsuarioMapper {
    public static UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) return null;

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getDataNascimento(),
                usuario.isAtivo(),
                FuncaoSistemaMapper.toResponse(usuario.getFuncaoSistema()),
                usuario.getDataCriacao(),
                FamiliaMapper.toResponse(usuario.getFamilia())
        );
    }

    public static List<UsuarioResponse> toResponse(List<Usuario> associados) {
        if (associados == null || associados.isEmpty()) return null;

        return associados.stream().map(UsuarioMapper::toResponse).toList();
    }

    public static Usuario toEntity(UsuarioCreateRequest dto) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setTelefone(dto.telefone());
        usuario.setAtivo(true);
        return usuario;
    }

    public static void updateEntity(UsuarioUpdateRequest dto, Usuario usuario) {
        if (dto == null || usuario == null) return;

        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        usuario.setFamilia(usuario.getFamilia());
        usuario.setAtivo(dto.ativo());
    }
}
