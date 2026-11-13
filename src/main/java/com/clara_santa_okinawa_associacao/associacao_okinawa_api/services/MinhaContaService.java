package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta.MinhaContaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta.MinhaContaSenhaRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.conta.MinhaContaResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.AlreadyExistsByEmailException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.MinhaContaValidacaoException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.FamiliaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.FuncaoSistemaMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;

@Service
public class MinhaContaService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public MinhaContaService(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public MinhaContaResponse consultar(Integer id) {
        return resposta(buscar(id));
    }

    @Transactional
    public MinhaContaResponse atualizar(Integer id, MinhaContaRequest dados) {
        Usuario usuario = buscar(id);
        if (!dados.email().equalsIgnoreCase(usuario.getEmail().trim())) {
            validarSenhaAtual(usuario, dados.senhaAtual());
            if (usuarios.existsByEmailIgnoreCaseAndIdNot(dados.email(), id)) {
                throw new AlreadyExistsByEmailException("Já existe uma conta com este e-mail.");
            }
        }
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setTelefone(dados.telefone());
        return resposta(usuarios.saveAndFlush(usuario));
    }

    @Transactional
    public void alterarSenha(Integer id, MinhaContaSenhaRequest dados) {
        Usuario usuario = buscar(id);
        validarSenhaAtual(usuario, dados.senhaAtual());
        if (dados.novaSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new MinhaContaValidacaoException("NOVA_SENHA_LONGA", "A nova senha deve ter no máximo 72 bytes em UTF-8.");
        }
        if (encoder.matches(dados.novaSenha(), usuario.getSenha())) {
            throw new MinhaContaValidacaoException("NOVA_SENHA_IGUAL", "A nova senha deve ser diferente da atual.");
        }
        usuario.setSenha(encoder.encode(dados.novaSenha()));
        usuario.concluirPrimeiroAcesso();
        usuarios.saveAndFlush(usuario);
    }

    private Usuario buscar(Integer id) {
        Usuario usuario = usuarios.buscarComPermissoes(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário sem acesso válido"));
        if (!usuario.isAtivo() || usuario.getFuncaoSistema() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário sem acesso válido");
        }
        return usuario;
    }

    private void validarSenhaAtual(Usuario usuario, String senha) {
        if (senha == null || senha.isBlank() || senha.getBytes(StandardCharsets.UTF_8).length > 72 || !encoder.matches(senha, usuario.getSenha())) {
            throw new MinhaContaValidacaoException("SENHA_ATUAL_INCORRETA", "Senha atual incorreta.");
        }
    }

    private MinhaContaResponse resposta(Usuario usuario) {
        return new MinhaContaResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(),
                usuario.getDataNascimento(), FuncaoSistemaMapper.toResponse(usuario.getFuncaoSistema()),
                FamiliaMapper.toResponse(usuario.getFamilia()));
    }
}
