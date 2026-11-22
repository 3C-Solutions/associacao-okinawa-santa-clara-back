package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Usuario;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.repositories.UsuarioRepository;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.exceptions.*;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.Locale;

@Service
public class PermissaoAgendaService {
    private final UsuarioRepository usuarioRepository;
    public PermissaoAgendaService(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }

    public Usuario usuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() ->
                new UsuarioAgendaNaoIdentificadoException( "Usuário não identificado"));
        if (!usuario.isAtivo() || usuario.getFuncaoSistema() == null) {
            throw new AgendaAcessoNegadoException( "Usuário sem função ativa");
        }
        return usuario;
    }

    public String papel(Usuario usuario) {
        String nomeFuncao = usuario.getFuncaoSistema().getNome();
        if (nomeFuncao == null) throw new AgendaAcessoNegadoException( "Função sem nome");
        String nome = Normalizer.normalize(nomeFuncao, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").trim().toUpperCase(Locale.ROOT).replace('_', ' ');
        // Fail-closed: conferir os nomes reais em funcao_sistema antes de liberar a tela.
        return switch (nome) {
            case "DIRETORIA", "DIRETOR" -> "DIRETORIA";
            case "DEPARTAMENTO", "RESPONSAVEL DEPARTAMENTO" -> "DEPARTAMENTO";
            case "ASSOCIADO RESPONSAVEL", "ASSOCIADO TITULAR" -> "FAMILIA";
            case "ASSOCIADO DEPENDENTE", "DEPENDENTE" -> "DEPENDENTE";
            default -> throw new AgendaAcessoNegadoException(
                    "Função ainda não mapeada para a agenda: " + nome);
        };
    }

    public void exigirDiretoria(Usuario usuario) {
        if (!"DIRETORIA".equals(papel(usuario))) {
            throw new AgendaAcessoNegadoException( "Ação exclusiva da Diretoria");
        }
    }

    public void exigirSolicitante(Usuario usuario) {
        String papel = papel(usuario);
        if (!"DEPARTAMENTO".equals(papel) && !"FAMILIA".equals(papel)) {
            throw new AgendaAcessoNegadoException( "Perfil não autorizado a solicitar reservas");
        }
    }
}