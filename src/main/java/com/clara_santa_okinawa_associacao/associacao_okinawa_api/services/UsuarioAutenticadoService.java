package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.UsuarioAutenticadoContextoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.auth.UsuarioAutenticadoResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncionalidadeSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.FuncaoSistemaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioAutenticadoContextoResponse consultar(Integer usuarioId) {
        Usuario usuario = usuarioRepository.buscarComPermissoes(usuarioId)
                .orElseThrow(this::usuarioSemAcesso);

        if (!usuario.isAtivo() || usuario.getFuncaoSistema() == null) {
            throw usuarioSemAcesso();
        }

        UsuarioAutenticadoResponse identificacao = new UsuarioAutenticadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                FuncaoSistemaMapper.toResponse(usuario.getFuncaoSistema())
        );

        List<String> permissoes = usuario.getFuncaoSistema().getFuncionalidades().stream()
                .map(FuncionalidadeSistema::getCodigo)
                .distinct()
                .sorted()
                .toList();

        return new UsuarioAutenticadoContextoResponse(identificacao, permissoes);
    }

    private ResponseStatusException usuarioSemAcesso() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário sem acesso válido");
    }
}
