package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioCreateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioResponse;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.usuario.UsuarioUpdateRequest;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Familia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.FuncaoSistema;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.AlreadyExistsByEmailException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.NotExistsByIdException;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.mappers.UsuarioMapper;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FamiliaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.FuncaoSistemaRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private static final int TAMANHO_SENHA = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final FamiliaRepository familiaRepository;
    private final FuncaoSistemaRepository funcaoSistemaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          FamiliaRepository familiaRepository,
                          FuncaoSistemaRepository funcaoSistemaRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.familiaRepository = familiaRepository;
        this.funcaoSistemaRepository = funcaoSistemaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return UsuarioMapper.toResponse(usuarios);
    }

    public UsuarioResponse buscarPorId(Integer id) {
        Usuario usuario =
                usuarioRepository.findById(id).orElseThrow(() -> new NotExistsByIdException(
                        "Usuário não encontrado! " + id));

        return UsuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse cadastrar(UsuarioCreateRequest request) {
        String emailNormalizado = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new AlreadyExistsByEmailException("Já existe um usuário com este e-mail " +
                    "cadastrado no sistema! " + emailNormalizado);
        }

        Integer idFamiliaDesejada = request.idFamilia();

        Familia familia =
                familiaRepository.findById(idFamiliaDesejada).orElseThrow(() -> new NotExistsByIdException("Familia não encontrada " + idFamiliaDesejada));

        Integer idFuncaoSistemaDesejada = request.idFuncaoSistema();

        FuncaoSistema funcaoSistema =
                funcaoSistemaRepository.findById(idFuncaoSistemaDesejada).orElseThrow(() -> new NotExistsByIdException(
                        "Função do sistema não encontrada " + idFuncaoSistemaDesejada));


        Usuario novoUsuario = UsuarioMapper.toEntity(request);
        novoUsuario.setFamilia(familia);
        novoUsuario.setSenha(passwordEncoder.encode(gerarSenha()));
        novoUsuario.setFuncaoSistema(funcaoSistema);
        novoUsuario.setDataCriacao(LocalDateTime.now());

        Usuario usuarioCadastrado = usuarioRepository.save(novoUsuario);
        return UsuarioMapper.toResponse(usuarioCadastrado);
    }

    public UsuarioResponse atualizar(Integer id, UsuarioUpdateRequest request) {
        Usuario usuarioParaAtualizar =
                usuarioRepository.findById(id).orElseThrow(() -> new NotExistsByIdException(
                        "Usuário não encontrado! " + id));

        Integer idFuncaoSistema = request.idFuncaoSistema();

        FuncaoSistema funcaoSistema =
                funcaoSistemaRepository.findById(idFuncaoSistema).orElseThrow(() -> new NotExistsByIdException(
                        "Função do sistema não encontrada " + idFuncaoSistema));

        usuarioParaAtualizar.setId(id);
        usuarioParaAtualizar.setFuncaoSistema(funcaoSistema);
        UsuarioMapper.updateEntity(request, usuarioParaAtualizar);

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioParaAtualizar);

        return UsuarioMapper.toResponse(usuarioAtualizado);
    }

    public void excluir(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NotExistsByIdException("Usuário não encontrado " + id);
        }

        usuarioRepository.deleteById(id);
    }

    private String gerarSenha() {
        char[] caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder builder = new StringBuilder(TAMANHO_SENHA);

        for (int i = 0; i < TAMANHO_SENHA; i++) {
            builder.append(caracteres[secureRandom.nextInt(caracteres.length)]);
        }

        return builder.toString();
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
